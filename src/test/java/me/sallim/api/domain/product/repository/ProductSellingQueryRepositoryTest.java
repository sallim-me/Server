package me.sallim.api.domain.product.repository;

import me.sallim.api.config.QueryDslConfig;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product_selling.dto.response.ProductSellingSummaryResponse;
import me.sallim.api.domain.product.model.*;
import me.sallim.api.domain.product_selling.model.ProductSelling;
import me.sallim.api.domain.product_selling.repository.ProductSellingQueryRepository;
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
        Member member = Member.builder()
                .username("test_user")
                .password("password")
                .nickname("테스터")
                .name("홍길동")
                .isBuyer(false)
                .build();
        entityManager.persist(member);

        // Create and persist Product
        Product product = Product.builder()
                .member(member)
                .title("Test Product")
                .content("Test Content")
                .isActive(true)
                .postType(PostTypeEnum.SELLING)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persist(product);

        // Create and persist ProductSelling
        ProductSelling productSelling = ProductSelling.builder()
                .product(product)
                .modelNumber("XYZ123")
                .modelName("Test Model")
                .brand("삼성")
                .price(1000)
                .userPrice(950)
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