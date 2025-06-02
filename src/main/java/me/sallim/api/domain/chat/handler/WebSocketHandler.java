package me.sallim.api.domain.chat.handler;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.global.security.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class WebSocketHandler implements ChannelInterceptor {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                // Authorization 헤더에서 JWT 토큰 추출
                String authToken = accessor.getFirstNativeHeader("Authorization");
                if (authToken != null && authToken.startsWith("Bearer ")) {
                    String token = authToken.substring(7);
                    
                    if (jwtTokenProvider.validateToken(token)) {
                        Long memberId = Long.valueOf(jwtTokenProvider.getSubject(token));
                        
                        // 사용자 정보 조회 및 Principal 설정
                        Member member = memberRepository.findById(memberId)
                                .orElse(null);
                        
                        if (member != null) {
                            Principal principal = new UsernamePasswordAuthenticationToken(
                                    member.getId().toString(), null, null);
                            accessor.setUser(principal);
                            
                            // Redis에 연결 상태 저장
                            String userKey = "user:" + member.getId() + ":connected";
                            redisTemplate.opsForValue().set(userKey, "connected", 30, TimeUnit.MINUTES);
                        }
                    }
                }
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                Principal user = accessor.getUser();
                if (user != null) {
                    String userId = user.getName();
                    String userKey = "user:" + userId + ":connected";
                    redisTemplate.delete(userKey);
                }
            }
        }

        return message;
    }
} 