package me.sallim.api.domain.chat.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendMessageRequest {
    private Long chatRoomId;
    private String content;
}
