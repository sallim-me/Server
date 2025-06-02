package me.sallim.api.domain.chat.listener;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.dto.ChatMessageDTO;
import me.sallim.api.domain.member.repository.MemberRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "spring.firebase.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class ChatMessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(ChatMessageDTO message) {
        String receiverKey = "user:" + message.getReceiverId() + ":connected";
        Boolean isConnected = redisTemplate.hasKey(receiverKey);

        if (Boolean.TRUE.equals(isConnected)) {
            // WebSocket으로 메시지 전송
            messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(),
                "/queue/messages",
                message
            );
        } else {
            // FCM으로 푸시 알림 전송
            try {
                String receiverToken = memberRepository.findById(message.getReceiverId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getFcmToken();

                if (receiverToken != null) {
                    Message fcmMessage = Message.builder()
                        .setNotification(Notification.builder()
                            .setTitle("새로운 메시지")
                            .setBody(message.getContent())
                            .build())
                        .setToken(receiverToken)
                        .build();

                    firebaseMessaging.send(fcmMessage);
                }
            } catch (Exception e) {
                // FCM 전송 실패 시 로깅
                e.printStackTrace();
            }
        }
    }
} 