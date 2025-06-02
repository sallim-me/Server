package me.sallim.api.domain.chat.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(name = "latest_chat_message_id")
    private Long latestChatMessageId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void updateLatestMessage(Long messageId) {
        this.latestChatMessageId = messageId;
    }
}
