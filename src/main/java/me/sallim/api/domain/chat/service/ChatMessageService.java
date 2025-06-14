package me.sallim.api.domain.chat.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.model.ChatMessage;
import me.sallim.api.domain.chat.repository.ChatMessageRepository;
import me.sallim.api.domain.chat.dto.response.ReceiveMessageDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final ChatRoomSessionService chatRoomSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Long sendMessage(Long chatRoomId, Long senderId, Long receiverId, String content) {
        ChatMessage message = ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .isRead(false)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        chatRoomService.updateLatestMessage(chatRoomId, savedMessage.getId());
        
        // 실시간 메시지 전송
        sendRealTimeMessage(savedMessage);
        
        return savedMessage.getId();
    }
    
    /**
     * 실시간 메시지 전송 및 알림 처리
     */
    private void sendRealTimeMessage(ChatMessage message) {
        ReceiveMessageDTO messageDTO = ReceiveMessageDTO.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoomId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.isRead())
                .build();

        // 채팅방 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chatroom/" + message.getChatRoomId(), messageDTO);
        
        // 수신자가 채팅방에 없다면 개인 알림 전송
        if (!chatRoomSessionService.isUserInChatRoom(message.getChatRoomId(), message.getReceiverId())) {
            // 개인 알림 채널로 전송 (FCM 알림을 위한 준비)
            messagingTemplate.convertAndSend("/topic/notification/" + message.getReceiverId(), messageDTO);
        }
    }

    /**
     * 상대방이 채팅방에 있는지 확인
     */
    public boolean isReceiverInChatRoom(Long chatRoomId, Long receiverId) {
        return chatRoomSessionService.isUserInChatRoom(chatRoomId, receiverId);
    }

    /**
     * 상대방이 온라인 상태인지 확인
     */
    public boolean isReceiverOnline(Long receiverId) {
        return chatRoomSessionService.isUserOnline(receiverId);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId);
    }

    @Transactional(readOnly = true)
    public ChatMessage getMessage(Long messageId) {
        return chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
    }
    
    @Transactional
    public void markMessagesAsRead(Long chatRoomId, Long userId) {
        // 해당 채팅방에서 사용자가 받은 읽지 않은 메시지들을 읽음 처리
        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomIdAndReceiverIdAndIsReadFalse(chatRoomId, userId);
        for (ChatMessage message : unreadMessages) {
            message.markAsRead();
        }
        chatMessageRepository.saveAll(unreadMessages);
    }
    
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long chatRoomId, Long userId) {
        return chatMessageRepository.countByChatRoomIdAndReceiverIdAndIsReadFalse(chatRoomId, userId);
    }
}
