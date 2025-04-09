package me.sallim.api.domain.product.repository;

import me.sallim.api.config.QueryDslConfig;
import me.sallim.api.domain.product.dto.response.ProductSellingSummaryResponse;
import me.sallim.api.domain.product.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, ProductSellingQueryRepository.class})
class ProductSellingQueryRepositoryTest {

    @Autowired
    private ProductSellingQueryRepository productSellingQueryRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Create and persist Product
        Product product = Product.builder()
                .title("Test Product")
                .content("Test Content")
                .isActive(true)
                .postType(PostTypeEnum.SELLING)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(product);

        // Create and persist ProductSelling
        ProductSelling productSelling = ProductSelling.builder()
                .productId(product.getId())
                .modelName("Test Model")
                .price(1000)
                .build();
        entityManager.persist(productSelling);

        // Create and persist ProductPhoto
        ProductPhoto productPhoto = ProductPhoto.builder()
                .productId(product.getId())
                .url("https://example.com/test.jpg")
                .build();
        entityManager.persist(productPhoto);

        // Set the product photo to the product
        product.setProductPhotoId(productPhoto.getId());
        entityManager.persist(product);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findAllProductSellingSummaries_shouldReturnSellingProducts() {
        // Act
        List<ProductSellingSummaryResponse> results = productSellingQueryRepository.findAllProductSellingSummaries();

        // Assert
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        ProductSellingSummaryResponse response = results.get(0);
        assertThat(response.getTitle()).isEqualTo("Test Product");
        assertThat(response.getModelName()).isEqualTo("Test Model");
        assertThat(response.getPrice()).isEqualTo(1000);
        assertThat(response.getThumbnailUrl()).isEqualTo("https://example.com/test.jpg");
    }
}