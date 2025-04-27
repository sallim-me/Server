package me.sallim.api.domain.product_buying.dto.request;

import lombok.Builder;
import me.sallim.api.domain.appliance.ApplianceType;

@Builder
public record UpdateProductBuyingRequest(
        String title,
        String content,
        int quantity,
        ApplianceType applianceType,
        int price
) {}
