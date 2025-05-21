package me.sallim.api.domain.product_selling_answer.repository;

import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product_selling_answer.model.ProductSellingAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSellingAnswerRepository extends JpaRepository<ProductSellingAnswer, Long> {
    List<ProductSellingAnswer> findByProduct(Product product);
    List<ProductSellingAnswer> findByProductId(Long productId);
    void deleteByProduct(Product product);
}
