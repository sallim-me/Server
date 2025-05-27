package me.sallim.api.domain.product_scrap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sallim.api.domain.product_scrap.model.Scrap;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapResponse {
    private Long id;
    private Long memberId;
    private String memberNickname;
    private Long productId;
    private String productTitle;
    private String memo;
    private LocalDateTime createdAt;

    public static ScrapResponse from(Scrap scrap) {
        return ScrapResponse.builder()
                .id(scrap.getId())
                .memberId(scrap.getMember().getId())
                .memberNickname(scrap.getMember().getNickname())
                .productId(scrap.getProduct().getId())
                .productTitle(scrap.getProduct().getTitle())
                .memo(scrap.getMemo())
                .createdAt(scrap.getCreatedAt())
                .build();
    }
}
