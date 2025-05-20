package me.sallim.api.domain.product_selling.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductSellingRequest {
    private String title;
    private String content;
    private ApplianceType applianceType;
    private boolean isActive;

    private String modelNumber;
    private String modelName;
    private String brand;
    private int price;
    private int userPrice;

    private List<ProductSellingAnswerRequest> answers;
}