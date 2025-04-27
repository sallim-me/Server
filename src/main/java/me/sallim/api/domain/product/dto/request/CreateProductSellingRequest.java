package me.sallim.api.domain.product.dto.request;

import lombok.*;
import me.sallim.api.domain.appliance.ApplianceType;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductSellingRequest {
    // Product
    private String title;
    private String content;
    private ApplianceType applianceType;

    // ProductSelling
    private String modelNumber;
    private String modelName;
    private String modelSpecification;
    private int price;
}
