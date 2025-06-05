package me.sallim.api.domain.product_selling.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.product_selling.dto.response.ProductSellingSummaryResponse;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product.model.QProduct;
import me.sallim.api.domain.product_photo.model.QProductPhoto;
import me.sallim.api.domain.product_selling.model.QProductSelling;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.types.Projections.constructor;

@Repository
@RequiredArgsConstructor
public class ProductSellingQueryRepository {
    private final JPAQueryFactory queryFactory;
}
