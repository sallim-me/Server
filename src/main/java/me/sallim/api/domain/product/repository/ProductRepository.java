package me.sallim.api.domain.product.repository;

import me.sallim.api.domain.product.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"photos"})
    Optional<Product> findWithPhotosById(Long id);
}
