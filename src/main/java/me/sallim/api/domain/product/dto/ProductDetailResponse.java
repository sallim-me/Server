package me.sallim.api.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private int price;
}
