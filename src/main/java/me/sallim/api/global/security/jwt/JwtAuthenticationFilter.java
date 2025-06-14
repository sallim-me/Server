package me.sallim.api.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.global.security.dto.CustomUserDetails;
import me.sallim.api.global.security.exception.MemberNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));

            if (token == null) {
                // 토큰이 없는 경우 - 공개 엔드포인트는 계속 진행, 보호된 엔드포인트는 나중에 AuthenticationEntryPoint에서 처리
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 검증 (예외 발생 시 catch 블록에서 처리)
            jwtTokenProvider.validateTokenWithException(token);
            
            Long memberId = Long.valueOf(jwtTokenProvider.getSubject(token));

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다"));

            CustomUserDetails userDetails = new CustomUserDetails(member);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (Exception e) {
            // 예외를 request attribute에 저장하여 AuthenticationEntryPoint에서 사용할 수 있도록 함
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }
}