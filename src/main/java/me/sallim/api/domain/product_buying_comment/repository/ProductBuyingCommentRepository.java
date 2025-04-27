package me.sallim.api.domain.product_buying_comment.repository;

import me.sallim.api.domain.product_buying_comment.model.ProductBuyingComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductBuyingCommentRepository extends JpaRepository<ProductBuyingComment, Long> {
    List<ProductBuyingComment> findByProductId(Long productId);
}