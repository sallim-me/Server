package me.sallim.api.domain.chat.dto.response;

import lombok.Builder;
import lombok.Data;
import me.sallim.api.domain.chat.model.ChatRoom;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomResponse {
    private Long id;
    private Long productId;
    private Long latestChatMessageId;
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .productId(chatRoom.getProductId())
                .latestChatMessageId(chatRoom.getLatestChatMessageId())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}
