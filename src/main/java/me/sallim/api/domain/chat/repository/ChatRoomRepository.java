package me.sallim.api.domain.chat.repository;

import me.sallim.api.domain.chat.dto.ChatRoomWithLastMessageDto;
import me.sallim.api.domain.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        SELECT new me.sallim.api.domain.chat.dto.ChatRoomWithLastMessageDto(
            cr.id, cr.productId, cr.sellerId, cr.buyerId,
            cm.id, cm.content, cm.senderId, cm.createdAt
        )
        FROM ChatRoom cr
        LEFT JOIN ChatMessage cm ON cm.chatRoomId = cr.id
        WHERE (cr.sellerId = :userId OR cr.buyerId = :userId)
          AND cm.createdAt = (
              SELECT MAX(m.createdAt)
              FROM ChatMessage m
              WHERE m.chatRoomId = cr.id
          )
    """)
    List<ChatRoomWithLastMessageDto> findChatRoomsWithLastMessageByUser(@Param("userId") Long userId);
}
