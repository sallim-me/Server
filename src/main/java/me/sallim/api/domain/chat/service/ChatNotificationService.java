package me.sallim.api.domain.chat.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.dto.ChatMessageDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    /**
     * 채팅방 참여자들에게 새 메시지 알림
     */
    public void notifyNewMessage(Long roomId, ChatMessageDTO messageDTO) {
        // 채팅방 구독자들에게 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/topic/room/" + roomId, messageDTO);
    }

    /**
     * 특정 사용자에게 개인 알림 전송
     */
    public void notifyUser(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            message
        );
    }

    /**
     * 읽지 않은 메시지 수 업데이트 알림
     */
    public void notifyUnreadCount(Long userId, Long roomId) {
        long unreadCount = chatMessageService.getUnreadMessageCount(roomId, userId);
        
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/user/" + userId + "/messages",
            "읽지 않은 메시지: " + unreadCount + "개"
        );
    }

    /**
     * 에러 메시지 전송
     */
    public void notifyError(Long userId, String errorMessage) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/errors",
            errorMessage
        );
    }
}
