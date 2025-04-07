package me.sallim.api.domain.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoomParticipant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_participant_id")
    private Long id;

    @Column
    private Long memberId;

    @Column
    private Long chatRoomId;

    @Column(columnDefinition = "TIMESTAMP") @CreationTimestamp // TODO: setting timezone
    private LocalDateTime joinedAt;

    @Column @ColumnDefault("true")
    private Boolean isActive;

    @Column
    private Long lastReadChatMessageId;
}
