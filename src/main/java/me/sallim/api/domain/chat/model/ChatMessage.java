package me.sallim.api.domain.chat.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    private Long chatRoomId;
    private Long senderId;
    private Long receiverId;
    private String content;

    // TODO: setting timezone
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
}
