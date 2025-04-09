package me.sallim.api.domain.chat.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_message") // TODO: index
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "content", columnDefinition = "varchar(2048)")
    private String content;

    // TODO: columnDefinition -> 어노테이션
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
