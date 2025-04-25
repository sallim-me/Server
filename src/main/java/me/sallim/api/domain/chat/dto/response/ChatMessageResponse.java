package me.sallim.api.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime createdAt;
}
