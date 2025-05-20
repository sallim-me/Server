package me.sallim.api.domain.product_selling.dto.response;

import lombok.Builder;
import me.sallim.api.domain.appliance.ApplianceType;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product_selling.model.ProductSelling;
import me.sallim.api.domain.product_selling_answer.dto.response.ProductSellingAnswerResponse;
import me.sallim.api.domain.product_selling_answer.model.ProductSellingAnswer;

import java.util.List;

@Builder
public record ProductSellingDetailResponse(
        String title,
        String content,
        boolean isActive,
        ApplianceType applianceType,
        String modelName,
        String modelNumber,
        String brand,
        int price,
        int userPrice,
        List<ProductSellingAnswerResponse> answers
) {
    public static ProductSellingDetailResponse from(ProductSelling selling, Product product, List<ProductSellingAnswer> answers) {
        return ProductSellingDetailResponse.builder()
                .title(product.getTitle())
                .content(product.getContent())
                .isActive(product.getIsActive())
                .applianceType(product.getApplianceType())
                .modelName(selling.getModelName())
                .modelNumber(selling.getModelNumber())
                .brand(selling.getBrand())
                .price(selling.getPrice())
                .userPrice(selling.getUserPrice())
                .answers(answers.stream().map(ProductSellingAnswerResponse::from).toList())
                .build();
    }
}