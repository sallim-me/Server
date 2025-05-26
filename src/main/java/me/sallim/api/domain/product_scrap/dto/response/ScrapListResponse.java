package me.sallim.api.domain.product_scrap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapListResponse {
    private List<ScrapResponse> scraps;
    private int totalPages;
    private long totalElements;

    public static ScrapListResponse from(Page<ScrapResponse> scrapPage) {
        return ScrapListResponse.builder()
                .scraps(scrapPage.getContent())
                .totalPages(scrapPage.getTotalPages())
                .totalElements(scrapPage.getTotalElements())
                .build();
    }
}
