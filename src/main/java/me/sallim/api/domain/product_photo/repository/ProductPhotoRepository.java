package me.sallim.api.domain.product_photo.repository;

import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product_photo.model.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {
    List<ProductPhoto> findByProduct(Product product);
    Optional<ProductPhoto> findByProductAndId(Product product, Long id);
    void deleteByProductAndId(Product product, Long id);
}
