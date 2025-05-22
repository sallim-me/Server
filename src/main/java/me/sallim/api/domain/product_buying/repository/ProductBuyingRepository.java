package me.sallim.api.domain.product_buying.repository;

import me.sallim.api.domain.product_buying.model.ProductBuying;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductBuyingRepository extends JpaRepository<ProductBuying, Long> {
    Optional<ProductBuying> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
