package me.sallim.api.domain.product_selling.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSellingSummaryResponse {
    private Long id;
    private String title;
    private String modelName;
    private int price;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
}
