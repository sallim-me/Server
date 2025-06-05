package me.sallim.api.domain.chat.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.model.ChatRoomParticipant;
import me.sallim.api.domain.chat.repository.ChatRoomParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomParticipantService {

    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatRoomService chatRoomService;

    @Transactional
    public void joinChatRoom(Long chatRoomId, Long memberId) {
        // 채팅방 존재 여부 확인
        chatRoomService.getChatRoom(chatRoomId);

        ChatRoomParticipant participant = ChatRoomParticipant.builder()
                .chatRoomId(chatRoomId)
                .memberId(memberId)
                .isActive(true)
                .build();

        chatRoomParticipantRepository.save(participant);
    }

    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long memberId) {
        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findByChatRoomIdAndMemberId(chatRoomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 참여자가 아닙니다."));

        participant.deactivate();
    }

    @Transactional
    public void updateLastReadMessage(Long chatRoomId, Long memberId, Long messageId) {
        ChatRoomParticipant participant = chatRoomParticipantRepository
                .findByChatRoomIdAndMemberId(chatRoomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 참여자가 아닙니다."));

        participant.updateLastReadMessage(messageId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomParticipant> getParticipants(Long chatRoomId) {
        return chatRoomParticipantRepository.findByChatRoomIdAndIsActiveTrue(chatRoomId);
    }

    @Transactional(readOnly = true)
    public boolean isParticipant(Long chatRoomId, Long memberId) {
        return chatRoomParticipantRepository
                .findByChatRoomIdAndMemberIdAndIsActiveTrue(chatRoomId, memberId)
                .isPresent();
    }

    @Transactional(readOnly = true)
    public Long getOtherParticipantId(Long chatRoomId, Long memberId) {
        return chatRoomParticipantRepository.findByChatRoomIdAndMemberIdNot(chatRoomId, memberId)
                .map(ChatRoomParticipant::getMemberId)
                .orElseThrow(() -> new IllegalArgumentException("다른 참여자를 찾을 수 없습니다."));
    }
}
