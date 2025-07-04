package me.sallim.api.domain.product_selling.dto.response;

import lombok.Builder;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product_selling.model.ProductSelling;
import me.sallim.api.domain.product_selling_answer.dto.response.ProductSellingAnswerResponse;
import me.sallim.api.domain.product_selling_answer.model.ProductSellingAnswer;

import java.util.List;

@Builder
public record ProductSellingDetailResponse(
        Long id,
        String title,
        String content,
        boolean isActive,
        ApplianceType applianceType,
        String modelName,
        String modelNumber,
        String brand,
        int price,
        int userPrice,
        List<ProductSellingAnswerResponse> answers,
        boolean isAuthor,
        String nickname
) {
    public static ProductSellingDetailResponse from(ProductSelling selling, Product product, List<ProductSellingAnswer> answers) {
        return ProductSellingDetailResponse.builder()
                .id(product.getId())
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
                .isAuthor(false) // Default value, will be updated in service if needed
                .nickname(product.getMember().getNickname())
                .build();
    }
}

