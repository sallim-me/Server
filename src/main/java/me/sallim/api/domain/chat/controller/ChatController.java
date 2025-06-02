package me.sallim.api.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.dto.ChatMessageDTO;
import me.sallim.api.domain.chat.dto.request.CreateChatRoomRequest;
import me.sallim.api.domain.chat.dto.request.SendMessageRequest;
import me.sallim.api.domain.chat.dto.response.ChatMessageResponse;
import me.sallim.api.domain.chat.dto.response.ReceiveMessageDTO;
import me.sallim.api.domain.chat.dto.response.ChatRoomResponse;
import me.sallim.api.domain.chat.model.ChatMessage;
import me.sallim.api.domain.chat.model.ChatRoom;
import me.sallim.api.domain.chat.service.ChatMessageService;
import me.sallim.api.domain.chat.service.ChatRoomParticipantService;
import me.sallim.api.domain.chat.service.ChatRoomService;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/rooms")
    @Operation(
        summary = "채팅방 생성",
        description = """
            새로운 채팅방을 생성합니다.
            ### 요청 예시 JSON:
            ```json
            {
                "productId": 1
            }
            ```
            ### 응답 예시:
            ```json
            {
                "id": 1,
                "productId": 1,
                "latestChatMessageId": null,
                "createdAt": "2024-03-20T15:30:00"
            }
            ```
            """
    )
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(
            @RequestBody CreateChatRoomRequest request,
            @LoginMember Member member) {
        ChatRoomResponse chatRoom = chatRoomService.createChatRoom(request.getProductId(), member.getId());
        return ResponseEntity.ok(ApiResponse.success(chatRoom));
    }

    @GetMapping("/rooms")
    @Operation(
        summary = "채팅방 목록 조회",
        description = """
            로그인한 사용자가 참여 중인 모든 채팅방을 조회합니다.
            ### 응답 예시:
            ```json
            [
                {
                    "id": 1,
                    "productId": 1,
                    "latestChatMessageId": 5,
                    "createdAt": "2024-03-20T15:30:00"
                }
            ]
            ```
            """
    )
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getChatRooms(@LoginMember Member member) {
        List<ChatRoom> chatRooms = chatRoomService.getChatRoomsByMemberId(member.getId());
        List<ChatRoomResponse> responses = chatRooms.stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/rooms/{roomId}/messages")
    @Operation(
        summary = "메시지 전송",
        description = """
            채팅방에 메시지를 전송합니다.
            ### Path Variable:
            - `roomId` : 메시지를 전송할 채팅방의 ID
            
            ### 요청 예시 JSON:
            ```json
            {
                "content": "안녕하세요!"
            }
            ```
            
            ### 응답 예시:
            ```json
            {
                "id": 1,
                "chatRoomId": 1,
                "senderId": 1,
                "content": "안녕하세요!",
                "createdAt": "2024-03-20T15:30:00"
            }
            ```
            """
    )
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @PathVariable Long roomId,
            @RequestBody SendMessageRequest request,
            @LoginMember Member member) {
        
        // 메시지 저장
        Long receiverId = chatRoomService.getOtherParticipantId(roomId, member.getId());
        Long messageId = chatMessageService.sendMessage(roomId, member.getId(), receiverId, request.getContent());
        ChatMessage message = chatMessageService.getMessage(messageId);
        
        // Kafka로 메시지 전송
        ChatMessageDTO messageDTO = ChatMessageDTO.builder()
                .chatRoomId(roomId)
                .senderId(member.getId())
                .receiverId(receiverId)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        
        kafkaTemplate.send("chat-messages", messageDTO);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, messageDTO);
        
        return ResponseEntity.ok(ApiResponse.success(ChatMessageResponse.from(message)));
    }

    @GetMapping("/rooms/{roomId}/messages")
    @Operation(
        summary = "채팅방 메시지 목록 조회",
        description = """
            특정 채팅방의 메시지 목록을 조회합니다.
            ### Path Variable:
            - `roomId` : 메시지를 조회할 채팅방의 ID
            
            ### 응답 예시:
            ```json
            [
                {
                    "id": 1,
                    "chatRoomId": 1,
                    "senderId": 1,
                    "receiverId": 2,
                    "content": "안녕하세요!",
                    "createdAt": "2024-03-20T15:30:00",
                    "isRead": true
                }
            ]
            ```
            """
    )
    public ResponseEntity<ApiResponse<List<ReceiveMessageDTO>>> getMessages(
            @PathVariable Long roomId,
            @LoginMember Member member) {
        List<ChatMessage> messages = chatMessageService.getMessages(roomId);
        List<ReceiveMessageDTO> responses = messages.stream()
                .map(ReceiveMessageDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(
        summary = "채팅방 조회",
        description = """
            특정 채팅방의 정보를 조회합니다.
            ### Path Variable:
            - `chatRoomId` : 조회할 채팅방의 ID

            ### 응답 예시:
            ```json
            {
                "id": 1,
                "productId": 1,
                "latestChatMessageId": 5,
                "createdAt": "2024-03-20T15:30:00"
            }
            ```
            """
    )
    @GetMapping("/room/{chatRoomId}")
    public ResponseEntity<?> getChatRoom(@PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        ChatRoomResponse response = ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .productId(chatRoom.getProductId())
                .latestChatMessageId(chatRoom.getLatestChatMessageId())
                .createdAt(chatRoom.getCreatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
        summary = "채팅방 나가기",
        description = """
            채팅방에서 나갑니다.
            ### Path Variable:
            - `chatRoomId` : 나갈 채팅방의 ID
            
            ### 응답 예시:
            ```json
            {
                "message": "채팅방을 나갔습니다."
            }
            ```
            """
    )
    @DeleteMapping("/room/{chatRoomId}/leave")
    public ResponseEntity<?> leaveChatRoom(
            @LoginMember Member member,
            @PathVariable Long chatRoomId) {
        chatRoomParticipantService.leaveChatRoom(chatRoomId, member.getId());
        return ResponseEntity.ok(ApiResponse.success("채팅방을 나갔습니다."));
    }

    @Operation(
        summary = "내가 참여 중인 모든 채팅방 조회",
        description = """
            로그인한 사용자가 참여 중인 모든 채팅방을 조회합니다.
            ### 응답 예시:
            ```json
            [
                {
                    "id": 1,
                    "productId": 1,
                    "latestChatMessageId": 5,
                    "createdAt": "2024-03-20T15:30:00"
                }
            ]
            ```
            """
    )
    @GetMapping("/my-rooms")
    public ResponseEntity<?> getMyChatRooms(@LoginMember Member member) {
        List<ChatRoom> chatRooms = chatRoomService.getMyChatRooms(member.getId());
        List<ChatRoomResponse> response = chatRooms.stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
        summary = "채팅방 읽음 처리",
        description = """
            채팅방의 읽지 않은 메시지들을 모두 읽음 처리합니다.
            ### Path Variable:
            - `roomId` : 읽음 처리할 채팅방의 ID
            
            ### 응답 예시:
            ```json
            {
                "message": "읽음 처리 완료"
            }
            ```
            """
    )
    @PutMapping("/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<String>> markRoomAsRead(
            @PathVariable Long roomId,
            @LoginMember Member member) {
        
        // 채팅방 메시지 읽음 처리
        chatMessageService.markMessagesAsRead(roomId, member.getId());
        
        // 마지막 읽은 메시지 업데이트 (최신 메시지 ID 사용)
        List<ChatMessage> messages = chatMessageService.getMessages(roomId);
        if (!messages.isEmpty()) {
            Long latestMessageId = messages.get(0).getId(); // DESC 정렬이므로 첫 번째가 최신
            chatRoomParticipantService.updateLastReadMessage(roomId, member.getId(), latestMessageId);
        }
        
        return ResponseEntity.ok(ApiResponse.success("읽음 처리 완료"));
    }
}
