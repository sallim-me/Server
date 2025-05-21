package me.sallim.api.domain.product_comment.dto.request;

import lombok.Builder;

@Builder
public record CreateProductCommentRequest(
        String content
) {}
