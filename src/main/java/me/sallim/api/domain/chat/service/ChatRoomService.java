package me.sallim.api.domain.chat.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.chat.dto.response.ChatRoomResponse;
import me.sallim.api.domain.chat.dto.response.ChatRoomWithUnreadCountResponse;
import me.sallim.api.domain.chat.model.ChatRoom;
import me.sallim.api.domain.chat.repository.ChatRoomRepository;
import me.sallim.api.domain.chat.repository.ChatRoomQueryRepository;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.repository.ProductRepository;
import me.sallim.api.domain.product.repository.ProductQueryRepository;
import me.sallim.api.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomQueryRepository chatRoomQueryRepository;
    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getChatRoomsByMemberId(Long memberId) {
        return chatRoomRepository.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getMyChatRooms(Long memberId) {
        return chatRoomRepository.findByMemberId(memberId);
    }

    @Transactional
    public void updateLatestMessage(Long chatRoomId, Long messageId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        chatRoom.updateLatestMessage(messageId);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        chatRoomRepository.delete(chatRoom);
    }

    @Transactional
    public ChatRoomResponse createChatRoom(Long productId, Long buyerId) {
        // 1. 상품 정보 조회 및 유효성 검사
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 상품이 삭제되었는지 확인
        if (product.isDeleted()) {
            throw new IllegalArgumentException("삭제된 상품입니다.");
        }

        // 판매자 ID 조회
        Long sellerId = productQueryRepository.findSellerIdById(product.getId());
        if (sellerId == null) {
            throw new IllegalArgumentException("판매자 정보를 찾을 수 없습니다.");
        }

        // 판매자 계정이 존재하는지 확인
        if (!memberRepository.existsById(sellerId)) {
            throw new IllegalArgumentException("판매자 계정이 존재하지 않습니다.");
        }

        // 구매자가 판매자와 동일한지 확인
        if (sellerId.equals(buyerId)) {
            throw new IllegalArgumentException("자신의 상품에는 채팅을 시작할 수 없습니다.");
        }

        // 2. 기존 채팅방 조회 (새로운 구조 사용)
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByProductIdAndSellerIdAndBuyerId(productId, sellerId, buyerId);
        
        if (existingChatRoom.isPresent()) {
            ChatRoom chatRoom = existingChatRoom.get();
            return ChatRoomResponse.builder()
                    .id(chatRoom.getId())
                    .productId(chatRoom.getProductId())
                    .latestChatMessageId(chatRoom.getLatestChatMessageId())
                    .createdAt(chatRoom.getCreatedAt())
                    .build();
        }

        // 3. 새로운 채팅방 생성 (sellerId, buyerId 포함)
        ChatRoom newChatRoom = ChatRoom.builder()
                .productId(productId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);

        return ChatRoomResponse.builder()
                .id(savedChatRoom.getId())
                .productId(savedChatRoom.getProductId())
                .latestChatMessageId(savedChatRoom.getLatestChatMessageId())
                .createdAt(savedChatRoom.getCreatedAt())
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<ChatRoomWithUnreadCountResponse> getMyChatRoomsWithUnreadCount(Long memberId) {
        return chatRoomQueryRepository.findByMemberIdWithUnreadCount(memberId);
    }
    
    @Transactional(readOnly = true)
    public Long getOtherParticipantId(Long chatRoomId, Long senderId) {
        ChatRoom chatRoom = getChatRoom(chatRoomId);
        
        if (chatRoom.getSellerId().equals(senderId)) {
            return chatRoom.getBuyerId();
        } else if (chatRoom.getBuyerId().equals(senderId)) {
            return chatRoom.getSellerId();
        } else {
            throw new IllegalArgumentException("발신자가 채팅방 참여자가 아닙니다.");
        }
    }
}
