package me.sallim.api.domain.chat.repository;

import me.sallim.api.domain.chat.dto.response.ChatRoomResponse;
import me.sallim.api.domain.chat.model.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
    Optional<ChatRoomParticipant> findByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);
    Optional<ChatRoomParticipant> findByChatRoomIdAndMemberIdAndIsActiveTrue(Long chatRoomId, Long memberId);
    List<ChatRoomParticipant> findByChatRoomIdAndIsActiveTrue(Long chatRoomId);
    List<ChatRoomParticipant> findByMemberIdAndIsActiveTrue(Long memberId);
    Optional<ChatRoomParticipant> findByChatRoomIdAndMemberIdNot(Long chatRoomId, Long memberId);
}
