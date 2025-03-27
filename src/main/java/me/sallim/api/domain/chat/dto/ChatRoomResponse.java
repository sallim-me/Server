package me.sallim.api.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomResponse {
    private Long id;
    private Long productId;
    private Long sellerId;
    private Long buyerId;
    private Long lastMessageSenderId;
    private String lastMessage;
    private String lastMessageCreatedAt;
    private LocalDateTime createdAt;
}
