package me.sallim.api.domain.chat.service;

import me.sallim.api.domain.chat.dto.response.ChatRoomResponse;
import me.sallim.api.domain.chat.model.ChatRoom;
import me.sallim.api.domain.chat.repository.ChatRoomRepository;
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
class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Product testProduct;
    private Member testMember;
    private Member testBuyer;
    private Long buyerId;

    @BeforeEach
    void setUp() {
        // Create test seller member
        testMember = Member.builder()
                .username("seller")
                .password("password123")
                .nickname("판매자")
                .name("김판매")
                .isBuyer(false)
                .build();
        memberRepository.save(testMember);
        
        // Create test buyer member
        testBuyer = Member.builder()
                .username("buyer")
                .password("password123")
                .nickname("구매자")
                .name("김구매")
                .isBuyer(true)
                .build();
        memberRepository.save(testBuyer);
        buyerId = testBuyer.getId();
        
        // Create test product
        testProduct = Product.builder()
                .member(testMember)
                .title("Test Product")
                .content("Test Content")
                .isActive(true)
                .postType(PostTypeEnum.SELLING)
                .applianceType(ApplianceType.REFRIGERATOR)
                .build();
        productRepository.save(testProduct);
    }

    @Test
    @Transactional
    void createChatRoom_shouldCreateNewChatRoom_whenNoExistingChatRoom() {
        // Act
        ChatRoomResponse response = chatRoomService.createChatRoom(testProduct.getId(), buyerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo(testProduct.getId());

        // Verify chat room was created
        List<ChatRoom> chatRooms = chatRoomRepository.findByProductId(testProduct.getId());
        assertThat(chatRooms).hasSize(1);
        assertThat(chatRooms.get(0).getId()).isEqualTo(response.getId());
        
        // Verify chat room has correct sellerId and buyerId
        ChatRoom chatRoom = chatRooms.get(0);
        assertThat(chatRoom.getSellerId()).isEqualTo(testMember.getId());
        assertThat(chatRoom.getBuyerId()).isEqualTo(buyerId);
    }

    @Test
    @Transactional
    void createChatRoom_shouldReturnExistingChatRoom_whenChatRoomExists() {
        // Arrange
        ChatRoom existingChatRoom = ChatRoom.builder()
                .productId(testProduct.getId())
                .sellerId(testMember.getId())
                .buyerId(buyerId)
                .build();
        chatRoomRepository.save(existingChatRoom);

        // Act
        ChatRoomResponse response = chatRoomService.createChatRoom(testProduct.getId(), buyerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(existingChatRoom.getId());
        assertThat(response.getProductId()).isEqualTo(testProduct.getId());

        // Verify no new chat room was created
        List<ChatRoom> chatRooms = chatRoomRepository.findByProductId(testProduct.getId());
        assertThat(chatRooms).hasSize(1);
    }
}