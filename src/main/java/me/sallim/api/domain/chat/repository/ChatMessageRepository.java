package me.sallim.api.domain.chat.repository;

import me.sallim.api.domain.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // Find unread messages for a user in a chat room
    long countByChatRoomIdAndReceiverIdAndIsReadFalse(Long chatRoomId, Long receiverId);

    // Find unread messages for a user in a chat room
    List<ChatMessage> findByChatRoomIdAndReceiverIdAndIsReadFalse(Long chatRoomId, Long receiverId);

    // Mark all messages as read for a user in a chat room
    // (Custom @Modifying query can be added if needed)
}
