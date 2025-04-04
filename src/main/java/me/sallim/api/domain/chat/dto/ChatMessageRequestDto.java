package me.sallim.api.domain.chat.dto;

import lombok.Getter;

@Getter
public class ChatMessageRequestDto {
    private Long chatRoomId;
    private Long senderId;
    private Long receiverId;
    private String content;
}
