package me.sallim.api.domain.product.dto.request;

import lombok.*;
import me.sallim.api.domain.appliance.ApplianceType;

import java.util.List;


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
    private String brand;
    private int price;
    private int userPrice;

    private List<ProductSellingAnswerRequest> answers; // 질문에 대한 사용자 답변
}
