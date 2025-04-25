package me.sallim.api.domain.chat.controller;

import me.sallim.api.domain.chat.dto.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.model.ChatRoom;
import me.sallim.api.domain.chat.service.ChatMessageService;
import me.sallim.api.domain.chat.service.ChatRoomParticipantService;
import me.sallim.api.domain.chat.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final ChatMessageService chatMessageService;
}
