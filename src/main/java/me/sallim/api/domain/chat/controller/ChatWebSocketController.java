package me.sallim.api.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.dto.ChatMessageDTO;
import me.sallim.api.domain.chat.model.ChatMessage;
import me.sallim.api.domain.chat.service.ChatMessageService;
import me.sallim.api.domain.chat.service.NotificationService;
import me.sallim.api.domain.chat.service.ChatRoomService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @MessageMapping("/chat/room/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, 
                          @Payload ChatMessageDTO messageDTO,
                          Principal principal) {
        try {
            // 현재 로그인한 사용자 ID 확인
            Long senderId = Long.valueOf(principal.getName());
            
            // 메시지 저장
            Long receiverId = chatRoomService.getOtherParticipantId(roomId, senderId);
            Long messageId = chatMessageService.sendMessage(
                roomId, 
                senderId, 
                receiverId, 
                messageDTO.getContent()
            );
            
            // 저장된 메시지 조회
            ChatMessage savedMessage = chatMessageService.getMessage(messageId);
            
            // DTO 업데이트
            ChatMessageDTO responseDTO = ChatMessageDTO.builder()
                    .id(savedMessage.getId())
                    .chatRoomId(roomId)
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .content(messageDTO.getContent())
                    .createdAt(savedMessage.getCreatedAt())
                    .build();
            
            // Kafka로 메시지 전송 (선택적)
            kafkaTemplate.send("chat-messages", responseDTO);
            
            // 알림 서비스를 통한 실시간 메시지 전송
            notificationService.sendChatNotification(savedMessage);
            
        } catch (Exception e) {
            // 에러 처리 로깅
            System.err.println("메시지 전송 실패: " + e.getMessage());
        }
    }
}
