package me.sallim.api.domain.chat.integration;

import me.sallim.api.domain.chat.dto.response.ChatRoomResponse;
import me.sallim.api.domain.chat.model.ChatMessage;
import me.sallim.api.domain.chat.model.ChatRoom;
import me.sallim.api.domain.chat.repository.ChatMessageRepository;
import me.sallim.api.domain.chat.repository.ChatRoomRepository;
import me.sallim.api.domain.chat.service.ChatMessageService;
import me.sallim.api.domain.chat.service.ChatRoomService;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ChatIntegrationTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Product testProduct;
    private Member seller;
    private Member buyer;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        // Create test seller
        seller = Member.builder()
                .username("seller_user")
                .password("password123")
                .nickname("판매자")
                .name("김판매")
                .isBuyer(false)
                .build();
        memberRepository.save(seller);

        // Create test buyer
        buyer = Member.builder()
                .username("buyer_user")
                .password("password123")
                .nickname("구매자")
                .name("김구매")
                .isBuyer(true)
                .build();
        memberRepository.save(buyer);

        // Create test product
        testProduct = Product.builder()
                .member(seller)
                .title("Integration Test Product")
                .content("Integration Test Content")
                .isActive(true)
                .postType(PostTypeEnum.SELLING)
                .applianceType(ApplianceType.REFRIGERATOR)
                .build();
        productRepository.save(testProduct);

        // Create chat room
        ChatRoomResponse chatRoomResponse = chatRoomService.createChatRoom(testProduct.getId(), buyer.getId());
        chatRoom = chatRoomRepository.findById(chatRoomResponse.getId()).orElseThrow();
    }

    @Test
    void integrationTest_chatFlow_shouldWorkCorrectly() {
        // Step 1: Verify chat room was created correctly
        assertThat(chatRoom).isNotNull();
        assertThat(chatRoom.getProductId()).isEqualTo(testProduct.getId());
        assertThat(chatRoom.getSellerId()).isEqualTo(seller.getId());
        assertThat(chatRoom.getBuyerId()).isEqualTo(buyer.getId());

        // Step 2: Send first message from buyer to seller
        Long firstMessageId = chatMessageService.sendMessage(
                chatRoom.getId(),
                buyer.getId(),
                seller.getId(),
                "안녕하세요, 이 상품에 관심이 있습니다."
        );

        ChatMessage firstMessage = chatMessageService.getMessage(firstMessageId);
        assertThat(firstMessage.getChatRoomId()).isEqualTo(chatRoom.getId());
        assertThat(firstMessage.getSenderId()).isEqualTo(buyer.getId());
        assertThat(firstMessage.getReceiverId()).isEqualTo(seller.getId());
        assertThat(firstMessage.getContent()).isEqualTo("안녕하세요, 이 상품에 관심이 있습니다.");
        assertThat(firstMessage.isRead()).isFalse();

        // Step 3: Send reply from seller to buyer
        Long secondMessageId = chatMessageService.sendMessage(
                chatRoom.getId(),
                seller.getId(),
                buyer.getId(),
                "네, 안녕하세요! 어떤 것이 궁금하신가요?"
        );

        ChatMessage secondMessage = chatMessageService.getMessage(secondMessageId);
        assertThat(secondMessage.getChatRoomId()).isEqualTo(chatRoom.getId());
        assertThat(secondMessage.getSenderId()).isEqualTo(seller.getId());
        assertThat(secondMessage.getReceiverId()).isEqualTo(buyer.getId());
        assertThat(secondMessage.getContent()).isEqualTo("네, 안녕하세요! 어떤 것이 궁금하신가요?");

        // Step 4: Verify messages are retrieved correctly
        List<ChatMessage> messages = chatMessageService.getMessages(chatRoom.getId());
        assertThat(messages).hasSize(2);
        
        // Messages should be ordered by creation time DESC (newest first)
        assertThat(messages.get(0).getId()).isEqualTo(secondMessageId);
        assertThat(messages.get(1).getId()).isEqualTo(firstMessageId);

        // Step 5: Test unread message count
        long unreadCountForSeller = chatMessageService.getUnreadMessageCount(chatRoom.getId(), seller.getId());
        long unreadCountForBuyer = chatMessageService.getUnreadMessageCount(chatRoom.getId(), buyer.getId());
        
        assertThat(unreadCountForSeller).isEqualTo(1); // First message not read by seller
        assertThat(unreadCountForBuyer).isEqualTo(1); // Second message not read by buyer

        // Step 6: Mark messages as read for seller
        chatMessageService.markMessagesAsRead(chatRoom.getId(), seller.getId());
        
        unreadCountForSeller = chatMessageService.getUnreadMessageCount(chatRoom.getId(), seller.getId());
        assertThat(unreadCountForSeller).isEqualTo(0);

        // Step 7: Verify buyer still has unread messages
        unreadCountForBuyer = chatMessageService.getUnreadMessageCount(chatRoom.getId(), buyer.getId());
        assertThat(unreadCountForBuyer).isEqualTo(1);

        // Step 8: Send one more message and verify total count
        chatMessageService.sendMessage(
                chatRoom.getId(),
                buyer.getId(),
                seller.getId(),
                "가격 협상이 가능할까요?"
        );

        List<ChatMessage> allMessages = chatMessageService.getMessages(chatRoom.getId());
        assertThat(allMessages).hasSize(3);
    }

    @Test
    void createChatRoom_duplicateCreation_shouldReturnSameRoom() {
        // Try to create another chat room with same product and buyer
        ChatRoomResponse duplicateResponse = chatRoomService.createChatRoom(testProduct.getId(), buyer.getId());
        
        // Should return the same chat room
        assertThat(duplicateResponse.getId()).isEqualTo(chatRoom.getId());
        
        // Verify only one chat room exists for this product-buyer combination
        List<ChatRoom> allChatRooms = chatRoomRepository.findByProductId(testProduct.getId());
        assertThat(allChatRooms).hasSize(1);
    }
}
