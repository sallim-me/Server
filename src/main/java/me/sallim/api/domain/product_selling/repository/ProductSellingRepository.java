package me.sallim.api.domain.product_selling.repository;

import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product_selling.model.ProductSelling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSellingRepository extends JpaRepository<ProductSelling, Long> {

    void delete(ProductSelling selling);
    Optional<ProductSelling> findByProduct(Product product);
}
