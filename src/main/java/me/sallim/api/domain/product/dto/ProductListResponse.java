package me.sallim.api.domain.product.dto;

import lombok.*;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.product.model.PostTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    private Long id;
    private String title;
    private Integer price;
    private Integer quantity;
    private PostTypeEnum tradeType; // "SELLING" or "BUYING"
    private ApplianceType category; // REFRIGERATOR, WASHING_MACHINE, AIR_CONDITIONER
    private String modelName; // 현재는 빈 문자열 (Product 테이블에 없음)
    private String priceOrQuantity; // 현재는 빈 문자열 (Product 테이블에 없음)
    private String description;
    private Boolean isScraped;
    private Boolean isActive;
    private Boolean isAuthor;
    private LocalDateTime createdAt;
    private Long memberId; // 작성자 ID (내부적으로만 사용)
    private String thumbnailUrl; // 썸네일 URL

    // QueryDSL Projections용 생성자 (isScraped, isAuthor 제외)
    public ProductListResponse(Long id, String title, Integer price, Integer quantity, PostTypeEnum tradeType,
                             ApplianceType category, String modelName, String description,
                             LocalDateTime createdAt, Long memberId, Boolean isActive, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.tradeType = tradeType;
        this.category = category;
        this.modelName = modelName;
        this.description = description;
        this.createdAt = createdAt;
        this.memberId = memberId;
        // isScraped와 isAuthor는 나중에 설정
        this.isScraped = false;
        this.isAuthor = false;
        this.isActive = isActive; // 기본값으로 true 설정
        this.thumbnailUrl = thumbnailUrl; // 썸네일 URL은 나중에 설정
    }
}
