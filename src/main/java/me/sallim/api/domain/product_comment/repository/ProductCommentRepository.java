package me.sallim.api.domain.product_comment.repository;

import me.sallim.api.domain.product_comment.model.ProductComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
    List<ProductComment> findByProductId(Long productId);
}