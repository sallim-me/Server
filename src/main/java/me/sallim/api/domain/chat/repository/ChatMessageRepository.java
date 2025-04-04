package me.sallim.api.domain.chat.repository;

import me.sallim.api.domain.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatRoomIdOrderByIdAsc(Long chatRoomId);
}
