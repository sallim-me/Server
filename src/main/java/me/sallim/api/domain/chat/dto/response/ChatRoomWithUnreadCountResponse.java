package me.sallim.api.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomWithUnreadCountResponse {
    private Long id;
    private Long productId;
    private String productTitle;
    private Long sellerId;
    private String otherUserNickname; // Changed from Long to String
    private Long buyerId;
    private Long latestChatMessageId;
    private LocalDateTime createdAt;
    private Long unreadCount;
    private String latestMessage;
    private LocalDateTime latestMessageTime;
}
