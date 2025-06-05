package me.sallim.api.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.dto.response.ChatRoomResponse;
import me.sallim.api.domain.chat.model.QChatMessage;
import me.sallim.api.domain.chat.model.QChatRoomParticipant;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ChatRoomResponse> findChatRoomsWithLastMessageByUserId(Long memberId) {
        QChatRoomParticipant chatRoomParticipant = QChatRoomParticipant.chatRoomParticipant;
        QChatMessage chatMessage = QChatMessage.chatMessage;


        return queryFactory
                .select(constructor(
                        ChatRoomResponse.class,
                        chatRoomParticipant.chatRoomId,
                        chatMessage.id,
                        chatMessage.senderId,
                        chatMessage.content,
                        chatMessage.createdAt
                ))
                .from(chatRoomParticipant)
                .leftJoin(chatMessage).on(chatRoomParticipant.lastReadChatMessageId.eq(chatMessage.id))
                .where(chatRoomParticipant.memberId.eq(memberId))
                .fetch();
    }
}
