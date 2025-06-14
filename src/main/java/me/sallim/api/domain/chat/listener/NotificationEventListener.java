package me.sallim.api.domain.chat.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sallim.api.domain.chat.service.NotificationService.NotificationEvent;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final MemberRepository memberRepository;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void handleNotificationEvent(NotificationEvent event) {
        try {
            log.info("Processing notification event for user: {}, type: {}", 
                    event.getUserId(), event.getType());

            switch (event.getType()) {
                case "CHAT_MESSAGE":
                    handleChatMessageNotification(event);
                    break;
                default:
                    log.warn("Unknown notification type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", event, e);
        }
    }

    private void handleChatMessageNotification(NotificationEvent event) {
        // FCM 토큰 조회
        Member receiver = memberRepository.findById(event.getUserId()).orElse(null);
        if (receiver == null) {
            log.warn("Receiver not found for user ID: {}", event.getUserId());
            return;
        }

        // FCM 토큰이 있는 경우에만 처리 (향후 FCM 연동을 위한 준비)
        String fcmToken = receiver.getFcmToken();
        if (fcmToken != null && !fcmToken.isEmpty()) {
            log.info("FCM token found for user: {}, preparing notification", event.getUserId());
            // TODO: Firebase 연동 시 이 부분에서 실제 FCM 메시지 전송
            // FirebaseMessaging.getInstance().send(createFcmMessage(event, fcmToken));
        } else {
            log.debug("No FCM token for user: {}, skipping push notification", event.getUserId());
        }
    }

    // TODO: FCM 연동 시 활성화
    /*
    private Message createFcmMessage(NotificationEvent event, String fcmToken) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("새로운 메시지")
                        .setBody(truncateMessage(event.getMessageContent()))
                        .build())
                .putData("chatRoomId", event.getChatRoomId().toString())
                .putData("senderId", event.getSenderId().toString())
                .putData("type", event.getType())
                .setToken(fcmToken)
                .build();
    }
    */

    private String truncateMessage(String message) {
        if (message == null) return "";
        return message.length() > 50 ? message.substring(0, 50) + "..." : message;
    }
}
