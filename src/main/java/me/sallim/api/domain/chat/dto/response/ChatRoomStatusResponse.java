package me.sallim.api.domain.chat.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomStatusResponse {
    private Long chatRoomId;
    private Long productId;
    private String productType;
    private String productTitle;
    private Long otherParticipantId;
    private boolean isOtherParticipantOnline;
    private boolean isOtherParticipantInRoom;
    private long unreadMessageCount;
}
