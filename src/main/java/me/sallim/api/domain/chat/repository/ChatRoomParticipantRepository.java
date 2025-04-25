package me.sallim.api.domain.chat.repository;

import me.sallim.api.domain.chat.dto.response.ChatRoomResponse;
import me.sallim.api.domain.chat.model.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
}
