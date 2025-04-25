package me.sallim.api.domain.chat.dto.request;

import lombok.Getter;

@Getter
public class ChatMessageRequest {
    private Long chatRoomId;
    private Long senderId;
    private Long receiverId;
    private String content;
}
