package me.sallim.api.domain.product.service;

import jakarta.transaction.Transactional;
import me.sallim.api.domain.appliance.ApplianceType;
import me.sallim.api.domain.product.dto.request.CreateProductSellingRequest;
import me.sallim.api.domain.product.dto.response.ProductSellingSummaryResponse;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.model.ProductSelling;
import me.sallim.api.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.product.repository.ProductSellingQueryRepository;
import me.sallim.api.domain.product.repository.ProductSellingRepository;
import me.sallim.api.domain.product_buying.repository.ProductBuyingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductBuyingRepository productBuyingRepository;
    private final ProductSellingRepository productSellingRepository;
    private final ProductSellingQueryRepository productSellingQueryRepository;

    public List<ProductSellingSummaryResponse> getSellingSummaries() {
        return productSellingQueryRepository.findAllProductSellingSummaries();
    }

    @Transactional
    public void createSellingProduct(Long memberId, CreateProductSellingRequest request) {
        // 1. Product 저장
        Product product = Product.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isActive(true)
                .postType(PostTypeEnum.SELLING)
                .applianceType(request.getApplianceType())
                .build();
        productRepository.save(product);

        // 2. ProductSelling 저장
        ProductSelling selling = ProductSelling.builder()
                .productId(product.getId()) // 연결
                .memberId(product.getMemberId()) // 필요시 setter or param
                .modelName(request.getModelName())
                .modelNumber(request.getModelNumber())
                .modelSpecification(request.getModelSpecification())
                .price(request.getPrice())
                .build();
        productSellingRepository.save(selling);
    }
}
