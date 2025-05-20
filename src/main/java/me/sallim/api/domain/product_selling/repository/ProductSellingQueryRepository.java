package me.sallim.api.domain.product_selling.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.product_selling.dto.response.ProductSellingSummaryResponse;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product.model.QProduct;
import me.sallim.api.domain.product.model.QProductPhoto;
import me.sallim.api.domain.product.model.QProductSelling;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;

@Repository
@RequiredArgsConstructor
public class ProductSellingQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ProductSellingSummaryResponse> findAllProductSellingSummaries() {
        QProduct p = QProduct.product;
        QProductSelling ps = QProductSelling.productSelling;
        QProductPhoto pp = QProductPhoto.productPhoto;

        return queryFactory
                .select(constructor(
                        ProductSellingSummaryResponse.class,
                        p.id,
                        p.title,
                        ps.modelName,
                        ps.price,
                        pp.url.as("thumbnailUrl"),
                        p.createdAt
                ))
                .from(p)
                .join(ps).on(ps.product.id.eq(p.id))
                .leftJoin(pp).on(pp.id.eq(p.productPhotoId))
                .where(
                        p.isActive.eq(true),
                        p.postType.eq(PostTypeEnum.SELLING)
                )
                .fetch();
    }
}
