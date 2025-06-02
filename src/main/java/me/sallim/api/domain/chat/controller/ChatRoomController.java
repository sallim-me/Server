package me.sallim.api.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.dto.response.ChatRoomResponse;
import me.sallim.api.domain.chat.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/products/{productId}/chat-rooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @PathVariable Long productId,
            @AuthenticationPrincipal Long memberId) {
        ChatRoomResponse response = chatRoomService.createChatRoom(productId, memberId);
        return ResponseEntity.ok(response);
    }
} 