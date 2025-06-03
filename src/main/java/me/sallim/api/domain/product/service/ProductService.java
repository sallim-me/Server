package me.sallim.api.domain.product.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.dto.ProductListResponse;
import me.sallim.api.domain.product.repository.ProductQueryRepository;
import me.sallim.api.domain.product_scrap.service.ScrapService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductQueryRepository productQueryRepository;
    private final ScrapService scrapService;
    
    public List<ProductListResponse> getAllProducts(Member currentMember) {
        List<ProductListResponse> products = productQueryRepository.findAllProducts();
        
        // 각 상품에 대해 isScraped 설정
        for (ProductListResponse product : products) {
            if (currentMember != null) {
                // 스크랩 여부 확인
                boolean isScraped = scrapService.isProductScrappedByMember(product.getId(), currentMember.getId());
                product.setIsScraped(isScraped);
            } else {
                product.setIsScraped(false);
            }
            // isAuthor 속성 제거
        }
        
        return products;
    }
}
