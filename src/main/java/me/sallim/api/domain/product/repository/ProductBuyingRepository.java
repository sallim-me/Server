package me.sallim.api.domain.product.repository;

import me.sallim.api.domain.product.model.ProductBuying;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductBuyingRepository extends JpaRepository<ProductBuying, Integer> {
}
