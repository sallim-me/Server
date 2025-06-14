package me.sallim.api.domain.member.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product.model.Product;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MemberPostSummaryResponse {
    private Long productId;
    private String title;
    private ApplianceType applianceType;
    private PostTypeEnum postType;
    private String thumbnailUrl;
    private boolean isActive;
    private LocalDateTime createdAt;

    public static MemberPostSummaryResponse from(Product product, String endpoint) {
        return MemberPostSummaryResponse.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .applianceType(product.getApplianceType())
                .postType(product.getPostType())
                .thumbnailUrl(endpoint + "/" + product.getProductPhotoId().getFileUrl())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}

