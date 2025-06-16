package me.sallim.api.domain.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.product.dto.ProductListResponse;
import me.sallim.api.domain.product.model.QProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    public List<ProductListResponse> findAllProducts() {
        QProduct product = QProduct.product;
        
        // Product 테이블만 사용하여 모든 상품 조회
        return queryFactory
                .select(Projections.constructor(ProductListResponse.class,
                        product.id,
                        product.title,
                        product.productSelling.price,
                        product.productBuying.quantity,
                        product.postType,
                        product.applianceType,
                        product.productSelling.modelName, // modelName은 product 테이블에 없으므로 빈 문자열
                        product.productSelling.modelNumber,
                        product.content,
                        product.createdAt,
                        product.member.id,
                        product.isActive,
                        product.productPhotoId.fileUrl
                ))
                .from(product)
                .leftJoin(product.productPhotoId)
                .leftJoin(product.productSelling)
                .leftJoin(product.productBuying)
                .orderBy(product.createdAt.desc())
                .fetch();
    }
    
    public Long findSellerIdById(Long productId) {
        QProduct product = QProduct.product;
        
        return queryFactory
                .select(product.member.id)
                .from(product)
                .where(product.id.eq(productId))
                .fetchOne();
    }
}
