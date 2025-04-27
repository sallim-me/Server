package me.sallim.api.domain.product_buying_comment.dto.request;

import lombok.Builder;

@Builder
public record CreateProductBuyingCommentRequest(
        String content
) {}
