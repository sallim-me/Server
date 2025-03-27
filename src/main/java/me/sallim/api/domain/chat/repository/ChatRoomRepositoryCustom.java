package me.sallim.api.domain.chat.repository;

import me.sallim.api.domain.chat.model.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface ChatRoomRepositoryCustom {
    List<ChatRoom> findMyChatRooms(Long userId);
}
