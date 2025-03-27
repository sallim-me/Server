package me.sallim.api.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomWithLastMessageDto(
        Long chatRoomId,
        Long productId,
        Long sellerId,
        Long buyerId,
        Long lastMessageId,
        String lastMessageContent,
        Long senderId,
        LocalDateTime sentAt
) {}
