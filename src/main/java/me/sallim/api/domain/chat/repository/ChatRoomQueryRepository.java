package me.sallim.api.domain.chat.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.dto.response.ChatRoomWithUnreadCountResponse;
import me.sallim.api.domain.chat.model.QChatMessage;
import me.sallim.api.domain.chat.model.QChatRoom;
import me.sallim.api.domain.member.model.QMember;
import me.sallim.api.domain.product.model.QProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 사용자가 참여중인 채팅방을 최신 메시지 순으로 조회
     * 읽지 않은 메시지 수도 함께 조회
     */
    public List<ChatRoomWithUnreadCountResponse> findByMemberIdWithUnreadCount(Long memberId) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatMessage chatMessage = QChatMessage.chatMessage;
        QMember member = QMember.member;
        QProduct product = QProduct.product;
        QChatMessage latestMessage = new QChatMessage("latestMessage");

        return queryFactory
                .select(Projections.constructor(ChatRoomWithUnreadCountResponse.class,
                        chatRoom.id,
                        chatRoom.productId,
                        JPAExpressions
                                .select(product.title)
                                .from(product)
                                .where(product.id.eq(chatRoom.productId)),
                        chatRoom.sellerId,
                        JPAExpressions
                                .select(member.nickname)
                                .from(member)
                                .where(member.id.eq(chatRoom.sellerId)
                                        .or(member.id.eq(chatRoom.buyerId))
                                        .and(member.id.ne(memberId))),
                        chatRoom.buyerId,
                        chatRoom.latestChatMessageId,
                        chatRoom.createdAt,
                        // 읽지 않은 메시지 수
                        JPAExpressions
                                .select(chatMessage.count())
                                .from(chatMessage)
                                .where(chatMessage.chatRoomId.eq(chatRoom.id)
                                        .and(chatMessage.isRead.eq(false))
                                        .and(chatMessage.senderId.ne(memberId))),
                        // 최신 메시지 내용
                        latestMessage.content,
                        // 최신 메시지 시간
                        latestMessage.createdAt
                ))
                .from(chatRoom)
                .leftJoin(latestMessage).on(latestMessage.id.eq(chatRoom.latestChatMessageId))
                .where(chatRoom.sellerId.eq(memberId).or(chatRoom.buyerId.eq(memberId)))
                .orderBy(latestMessage.createdAt.desc().nullsLast())
                .fetch();
    }
}
