package me.sallim.api.domain.product_buying.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.appliance.ApplianceType;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.repository.ProductRepository;
import me.sallim.api.domain.product_buying.dto.request.CreateProductBuyingRequest;
import me.sallim.api.domain.product_buying.dto.request.UpdateProductBuyingRequest;
import me.sallim.api.domain.product_buying.dto.response.ProductBuyingDetailResponse;
import me.sallim.api.domain.product_buying.model.ProductBuying;
import me.sallim.api.domain.product_buying.repository.ProductBuyingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductBuyingService {

    private final ProductRepository productRepository;
    private final ProductBuyingRepository productBuyingRepository;

    @Transactional
    public ProductBuyingDetailResponse createProductBuying(Member loginMember, CreateProductBuyingRequest request) {
        if (!Boolean.TRUE.equals(loginMember.getIsBuyer())) {
            throw new IllegalArgumentException("바이어 회원만 구매 글을 작성할 수 있습니다.");
        }
        if (request.quantity() < 3) {
            throw new IllegalArgumentException("구매 수량은 최소 3개 이상이어야 합니다.");
        }

        // Product 저장
        Product product = Product.builder()
                .memberId(loginMember.getId())
                .applianceType(request.applianceType())
                .title(request.title())
                .content(request.content())
                .isActive(true)
                .postType(PostTypeEnum.BUYING)
                .build();
        productRepository.save(product);

        // ProductBuying 저장
        ProductBuying productBuying = ProductBuying.builder()
                .product(product)
                .quantity(request.quantity())
                .price(request.price())
                .build();
        productBuyingRepository.save(productBuying);

        return ProductBuyingDetailResponse.from(productBuying);
    }

    @Transactional(readOnly = true)
    public ProductBuyingDetailResponse getProductBuyingDetail(Long productBuyingId) {
        ProductBuying productBuying = productBuyingRepository.findById(productBuyingId)
                .orElseThrow(() -> new IllegalArgumentException("구매 글을 찾을 수 없습니다."));
        return ProductBuyingDetailResponse.from(productBuying);
    }

    @Transactional
    public void deleteProductBuying(Member loginMember, Long productBuyingId) {
        ProductBuying productBuying = productBuyingRepository.findById(productBuyingId)
                .orElseThrow(() -> new IllegalArgumentException("구매 글을 찾을 수 없습니다."));
        if (!productBuying.getProduct().getMemberId().equals(loginMember.getId())) {
            throw new IllegalArgumentException("본인이 작성한 글만 삭제할 수 있습니다.");
        }
        productBuyingRepository.delete(productBuying);
    }

    @Transactional
    public ProductBuyingDetailResponse updateProductBuying(Member loginMember, Long productBuyingId, UpdateProductBuyingRequest request) {
        ProductBuying productBuying = productBuyingRepository.findById(productBuyingId)
                .orElseThrow(() -> new IllegalArgumentException("구매 글을 찾을 수 없습니다."));
        if (!productBuying.getProduct().getMemberId().equals(loginMember.getId())) {
            throw new IllegalArgumentException("본인이 작성한 글만 수정할 수 있습니다.");
        }
        productBuying.update(
                request.quantity(),
                request.price()
        );
        productBuying.getProduct().updateProductInfo(
                request.title(),
                request.content(),
                request.applianceType()
        );

        return ProductBuyingDetailResponse.from(productBuying);
    }

}
