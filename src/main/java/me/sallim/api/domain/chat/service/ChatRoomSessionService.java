package me.sallim.api.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ChatRoomSessionService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 사용자가 특정 채팅방에 입장해 있는지 확인
     */
    public boolean isUserInChatRoom(Long chatRoomId, Long userId) {
        String chatRoomUserKey = "chatroom:" + chatRoomId + ":user:" + userId;
        return redisTemplate.hasKey(chatRoomUserKey);
    }

    /**
     * 사용자가 온라인 상태인지 확인
     */
    public boolean isUserOnline(Long userId) {
        String userKey = "user:" + userId + ":connected";
        return redisTemplate.hasKey(userKey);
    }

    /**
     * 채팅방에 입장한 모든 사용자 조회
     */
    public Set<String> getUsersInChatRoom(Long chatRoomId) {
        String pattern = "chatroom:" + chatRoomId + ":user:*";
        return redisTemplate.keys(pattern);
    }

    /**
     * 사용자의 채팅방 입장 상태 갱신 (heartbeat)
     */
    public void refreshUserInChatRoom(Long chatRoomId, Long userId) {
        String chatRoomUserKey = "chatroom:" + chatRoomId + ":user:" + userId;
        redisTemplate.opsForValue().set(chatRoomUserKey, "active", 30, TimeUnit.MINUTES);
    }

    /**
     * 사용자를 채팅방에서 제거
     */
    public void removeUserFromChatRoom(Long chatRoomId, Long userId) {
        String chatRoomUserKey = "chatroom:" + chatRoomId + ":user:" + userId;
        redisTemplate.delete(chatRoomUserKey);
    }

    /**
     * 특정 채팅방에 입장한 사용자 수 조회
     */
    public Long getActiveUserCountInChatRoom(Long chatRoomId) {
        String pattern = "chatroom:" + chatRoomId + ":user:*";
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? (long) keys.size() : 0L;
    }
}
