package me.sallim.api.domain.product.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.dto.ProductListResponse;
import me.sallim.api.domain.product.repository.ProductQueryRepository;
import me.sallim.api.domain.product_scrap.service.ScrapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    @Value("${spring.minio.endpoint}")
    private String endpoint;
    
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

            // thumbnail URL 설정
            if (product.getThumbnailUrl() != null) {
                product.setThumbnailUrl(endpoint + "/" + product.getThumbnailUrl());
            } else {
                // 썸네일이 없는 경우 기본 이미지 URL 설정 (필요시)
//                product.setThumbnailUrl(endpoint + "/default-thumbnail.png");
            }
        }
        
        return products;
    }
}
