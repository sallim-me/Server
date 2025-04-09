package me.sallim.api.domain.product.repository;

import me.sallim.api.domain.product.model.ProductSelling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSellingRepository extends JpaRepository<ProductSelling, Long> {
}
