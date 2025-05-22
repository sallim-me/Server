package me.sallim.api.domain.product_buying.dto.response;

import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.product_buying.model.ProductBuying;

public record ProductBuyingDetailResponse(
        String title,
        String content,
        int quantity,
        ApplianceType applianceType,
        int price,
        boolean isActive
) {
    public static ProductBuyingDetailResponse from(ProductBuying productBuying) {
        return new ProductBuyingDetailResponse(
                productBuying.getProduct().getTitle(),
                productBuying.getProduct().getContent(),
                productBuying.getQuantity(),
                productBuying.getProduct().getApplianceType(),
                productBuying.getPrice(),
                productBuying.getProduct().getIsActive()
        );
    }
}
