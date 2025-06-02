package me.sallim.api.domain.product_scrap.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapRequest {

    @NotNull
    private Long productId;

    private String memo;
}
