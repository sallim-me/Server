package me.sallim.api.domain.chat.controller;

import me.sallim.api.domain.chat.dto.ChatMessageRequestDto;
import me.sallim.api.domain.chat.dto.ChatRoomResponse;
import me.sallim.api.domain.chat.dto.ChatRoomWithLastMessageDto;
import me.sallim.api.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/room")
    public ResponseEntity<List<ChatRoomWithLastMessageDto>> getRooms() {
        return ResponseEntity.ok(chatService.getRooms(1L));
    }

    @MessageMapping("/room")
//    @SendTo("/topic/chat.{chatRoomId}")
    public void sendMessageToRoom(ChatMessageRequestDto message) {
//        System.out.println("Received message: " + response);
        simpMessagingTemplate.convertAndSend(
                "/topic/chat." + message.getChatRoomId(),
                message
        );
    }
}
