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
}
