package me.sallim.api.domain.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @Column
    private Long chatRoomId;

    @Column
    private Long senderId;

    @Column(columnDefinition = "varchar(2048)")
    private String content;

    @Column(columnDefinition = "TIMESTAMP") @CreationTimestamp // TODO: setting timezone
    private LocalDateTime createdAt;
}
