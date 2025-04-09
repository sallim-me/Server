package me.sallim.api.domain.product.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductSellingRequest {
    // Product
    private String title;
    private String content;
    // ProductSelling
    private String modelNumber;
    private String modelName;
    private String modelSpecification;
    private int price;
}
