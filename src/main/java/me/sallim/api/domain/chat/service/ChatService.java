package me.sallim.api.domain.chat.service;

import me.sallim.api.domain.chat.dto.ChatRoomResponse;
import me.sallim.api.domain.chat.dto.ChatRoomWithLastMessageDto;
import me.sallim.api.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public List<ChatRoomWithLastMessageDto> getRooms(Long userId) {
        return chatRoomRepository.findChatRoomsWithLastMessageByUser(userId).stream()
                .toList();
    }
}
