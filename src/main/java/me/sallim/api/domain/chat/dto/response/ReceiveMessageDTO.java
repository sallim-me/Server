package me.sallim.api.domain.chat.dto.response;

import lombok.Builder;
import lombok.Data;
import me.sallim.api.domain.chat.model.ChatMessage;

import java.time.LocalDateTime;

@Data
@Builder
public class ReceiveMessageDTO {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;

    public static ReceiveMessageDTO from(ChatMessage message) {
        return ReceiveMessageDTO.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoomId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.isRead())
                .build();
    }
}
