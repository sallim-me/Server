package me.sallim.api.domain.product_buying.dto.request;

import lombok.Builder;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;

@Builder
public record UpdateProductBuyingRequest(
        String title,
        String content,
        int quantity,
        ApplianceType applianceType,
        Boolean isActive
) {}
