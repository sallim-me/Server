package me.sallim.api.domain.chat.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.model.ChatMessage;
import me.sallim.api.domain.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

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
        
        return savedMessage.getId();
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
