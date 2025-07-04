package me.sallim.api.domain.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_room_participant") // TODO: index
public class ChatRoomParticipant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_participant_id")
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_read_chat_message_id")
    private Long lastReadChatMessageId;

    @PrePersist
    protected void onCreate() {
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateLastReadMessage(Long messageId) {
        this.lastReadChatMessageId = messageId;
    }
}
