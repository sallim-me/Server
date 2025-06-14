package me.sallim.api.config;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.global.security.handler.CustomAccessDeniedHandler;
import me.sallim.api.global.security.handler.CustomAuthenticationEntryPoint;
import me.sallim.api.global.security.jwt.JwtAuthenticationFilter;
import me.sallim.api.global.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories(basePackages = "me.sallim.api.global.security.jwt")
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    @Value("${spring.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        if ("dev".equals(activeProfile)) {
            List<String> origins = new ArrayList<>(Arrays.asList(
                    "http://localhost:3000", // React app local
                    "http://127.0.0.1:3000", // React app local
                    "http://localhost:8080", // API local
                    "http://127.0.0.1:8080" // API local
            ));
            origins.addAll(Arrays.asList(allowedOrigins.split(","))); // Additional allowed origins
            configuration.setAllowedOrigins(origins);
        }
        if ("prod".equals(activeProfile)) {
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private String[] getPermittedPaths() {
        List<String> paths = new ArrayList<>(Arrays.asList(                "/crawler/**",
                "/auth/**",
                "/member/profile",
                "/health-check",
                "/ws-chat/**", // WebSocket endpoint
                "/product/all",
                "/test/public", // 테스트용 공개 엔드포인트
                "/health-check"));

        if ("dev".equals(activeProfile)) {
            paths.addAll(Arrays.asList(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs.yaml",
                    "/v3/api-docs/**",
                    "/v3/api-docs**",
                    "/swagger-resources/**",
                    "/ai/**", // AI 이미지 분석 API
                    "/webjars/**"));
        }
        return paths.toArray(new String[0]);
    }    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(getPermittedPaths()).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, memberRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
