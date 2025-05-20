package me.sallim.api.domain.member.dto.response;


import lombok.Builder;
import me.sallim.api.domain.appliance.ApplianceType;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product.model.Product;

import java.time.LocalDateTime;

@Builder
public record MemberPostSummaryResponse(
        Long productId,
        String title,
        ApplianceType applianceType,
        PostTypeEnum postType,
        boolean isActive,
        LocalDateTime createdAt
) {
    public static MemberPostSummaryResponse from(Product product) {
        return MemberPostSummaryResponse.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .applianceType(product.getApplianceType())
                .postType(product.getPostType())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}