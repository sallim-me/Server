package me.sallim.api.domain.chat.repository;

import me.sallim.api.domain.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByProductId(Long productId);

    // Find chat room by product, seller, and buyer
    Optional<ChatRoom> findByProductIdAndSellerIdAndBuyerId(Long productId, Long sellerId, Long buyerId);

    // Find all chat rooms where the user is either seller or buyer
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.sellerId = :memberId OR cr.buyerId = :memberId")
    List<ChatRoom> findByMemberId(@Param("memberId") Long memberId);

    // Find product ID by chat room ID
    @Query("SELECT cr.productId FROM ChatRoom cr WHERE cr.id = :chatRoomId")
    Optional<Long> findProductIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // Find product title by chat room ID
    @Query("SELECT p.title FROM ChatRoom cr JOIN Product p ON cr.productId = p.id WHERE cr.id = :chatRoomId")
    Optional<String> findProductTitleByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // Find product type by chat room ID
    @Query("SELECT p.postType FROM ChatRoom cr JOIN Product p ON cr.productId = p.id WHERE cr.id = :chatRoomId")
    Optional<String> findProductTypeByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}
