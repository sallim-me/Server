package me.sallim.api.domain.product.dto;

import lombok.Getter;

@Getter
public class ProductCreateRequest {
    private String name;
    private String description;
    private int price;
}
