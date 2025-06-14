package me.sallim.api.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sallim.api.domain.chat.dto.response.ReceiveMessageDTO;
import me.sallim.api.domain.chat.model.ChatMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ChatRoomSessionService chatRoomSessionService;

    /**
     * 채팅 메시지에 대한 실시간 알림 처리
     */
    public void sendChatNotification(ChatMessage message) {
        // 실시간 메시지 DTO 생성
        ReceiveMessageDTO messageDTO = ReceiveMessageDTO.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoomId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.isRead())
                .build();

        // 1. 채팅방 구독자들에게 실시간 메시지 전송
        messagingTemplate.convertAndSend("/topic/chatroom/" + message.getChatRoomId(), messageDTO);
        
        // 2. 수신자가 채팅방에 없다면 개인 알림 전송
        if (!chatRoomSessionService.isUserInChatRoom(message.getChatRoomId(), message.getReceiverId())) {
            sendPersonalNotification(message, messageDTO);
        }
    }

    /**
     * 개인 알림 전송 (푸시 알림 준비)
     */
    private void sendPersonalNotification(ChatMessage message, ReceiveMessageDTO messageDTO) {
        try {
            // 웹소켓을 통한 개인 알림
            messagingTemplate.convertAndSend("/topic/notification/" + message.getReceiverId(), messageDTO);
            
            // Kafka를 통한 FCM 알림 큐잉 (향후 FCM 연동을 위한 준비)
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .userId(message.getReceiverId())
                    .chatRoomId(message.getChatRoomId())
                    .senderId(message.getSenderId())
                    .messageContent(message.getContent())
                    .type("CHAT_MESSAGE")
                    .build();
            
            kafkaTemplate.send("notification-events", notificationEvent);
            log.info("Notification event sent for user: {}, chatRoom: {}", 
                    message.getReceiverId(), message.getChatRoomId());
            
        } catch (Exception e) {
            log.error("Failed to send notification for message: {}", message.getId(), e);
        }
    }

    /**
     * 알림 이벤트 DTO
     */
    public static class NotificationEvent {
        private Long userId;
        private Long chatRoomId;
        private Long senderId;
        private String messageContent;
        private String type;
        private Long timestamp;

        public static NotificationEventBuilder builder() {
            return new NotificationEventBuilder();
        }

        public static class NotificationEventBuilder {
            private Long userId;
            private Long chatRoomId;
            private Long senderId;
            private String messageContent;
            private String type;

            public NotificationEventBuilder userId(Long userId) {
                this.userId = userId;
                return this;
            }

            public NotificationEventBuilder chatRoomId(Long chatRoomId) {
                this.chatRoomId = chatRoomId;
                return this;
            }

            public NotificationEventBuilder senderId(Long senderId) {
                this.senderId = senderId;
                return this;
            }

            public NotificationEventBuilder messageContent(String messageContent) {
                this.messageContent = messageContent;
                return this;
            }

            public NotificationEventBuilder type(String type) {
                this.type = type;
                return this;
            }

            public NotificationEvent build() {
                NotificationEvent event = new NotificationEvent();
                event.userId = this.userId;
                event.chatRoomId = this.chatRoomId;
                event.senderId = this.senderId;
                event.messageContent = this.messageContent;
                event.type = this.type;
                event.timestamp = System.currentTimeMillis();
                return event;
            }
        }

        // Getters
        public Long getUserId() { return userId; }
        public Long getChatRoomId() { return chatRoomId; }
        public Long getSenderId() { return senderId; }
        public String getMessageContent() { return messageContent; }
        public String getType() { return type; }
        public Long getTimestamp() { return timestamp; }
    }
}
