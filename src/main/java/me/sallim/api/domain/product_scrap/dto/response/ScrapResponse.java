package me.sallim.api.domain.product_scrap.dto.response;

import lombok.*;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product_scrap.model.Scrap;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ScrapResponse {
    private Long id;
    private Long memberId;
    private String memberNickname;
    private Long productId;
    private PostTypeEnum postType;
    private Integer productPrice;
    private String productTitle;
    private String thumbnailUrl;
    private String memo;
    private LocalDateTime createdAt;

    public static ScrapResponse from(Scrap scrap, String minioEndpoint) {
        String thumbnailUrl = null;
        if (scrap.getProduct().getProductPhotoId() != null) {
            thumbnailUrl = minioEndpoint + "/" + scrap.getProduct().getProductPhotoId().getFileUrl();
        }

        return ScrapResponse.builder()
                .id(scrap.getId())
                .memberId(scrap.getMember().getId())
                .memberNickname(scrap.getMember().getNickname())
                .productId(scrap.getProduct().getId())
                .postType(scrap.getProduct().getPostType())
                .productPrice(
                        scrap.getProduct().getProductSelling() != null ?
                                scrap.getProduct().getProductSelling().getPrice()
                                : null
                )
                .productTitle(scrap.getProduct().getTitle())
                .thumbnailUrl(thumbnailUrl)
                .memo(scrap.getMemo())
                .createdAt(scrap.getCreatedAt())
                .build();
    }
}
