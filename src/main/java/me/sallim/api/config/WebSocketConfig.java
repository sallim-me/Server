package me.sallim.api.config;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.handler.WebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketHandler webSocketHandler;

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    @Value("${spring.cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        List<String> origins =new ArrayList<>();

        if ("dev".equals(activeProfile)) {
            origins = new ArrayList<>(Arrays.asList(
                    "http://localhost:3000", // React app local
                    "http://127.0.0.1:3000", // React app local
                    "http://localhost:8080", // API local
                    "http://127.0.0.1:8080" // API local
            ));
            origins.addAll(Arrays.asList(allowedOrigins.split(","))); // Additional allowed origins
        }
        if ("prod".equals(activeProfile)) {
            origins = new ArrayList<>(Arrays.asList(allowedOrigins.split(",")));
        }

        registry.addEndpoint("/ws-chat")
//                .setAllowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
                .setAllowedOrigins(origins.toArray(new String[0]))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 구독할 수 있는 경로
        registry.enableSimpleBroker("/topic", "/queue");
        
        // 클라이언트가 메시지를 보낼 때 사용할 경로 프리픽스
        registry.setApplicationDestinationPrefixes("/app");
        
        // 사용자별 개인 메시지용 프리픽스
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketHandler);
    }
}
