package me.sallim.api.domain.chat.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatRoomRequest {
    private Long productId;
}
