package me.sallim.api.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.product.model.PostTypeEnum;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    private Long id;
    private String title;
    private PostTypeEnum tradeType; // "SELLING" or "BUYING"
    private ApplianceType category; // REFRIGERATOR, WASHING_MACHINE, AIR_CONDITIONER
    private String modelName; // 현재는 빈 문자열 (Product 테이블에 없음)
    private String priceOrQuantity; // 현재는 빈 문자열 (Product 테이블에 없음)
    private String description;
    private Boolean isScraped;
    private Boolean isAuthor;
    private LocalDateTime createdAt;
    private Long memberId; // 작성자 ID (내부적으로만 사용)
    
    // QueryDSL Projections용 생성자 (isScraped, isAuthor 제외)
    public ProductListResponse(Long id, String title, PostTypeEnum tradeType, 
                             ApplianceType category, String modelName, 
                             String priceOrQuantity, String description, 
                             LocalDateTime createdAt, Long memberId) {
        this.id = id;
        this.title = title;
        this.tradeType = tradeType;
        this.category = category;
        this.modelName = modelName;
        this.priceOrQuantity = priceOrQuantity;
        this.description = description;
        this.createdAt = createdAt;
        this.memberId = memberId;
        // isScraped와 isAuthor는 나중에 설정
        this.isScraped = false;
        this.isAuthor = false;
    }
    
    public void setIsScraped(Boolean isScraped) {
        this.isScraped = isScraped;
    }
    
    public void setIsAuthor(Boolean isAuthor) {
        this.isAuthor = isAuthor;
    }
    
    public Long getMemberId() {
        return memberId;
    }
}
