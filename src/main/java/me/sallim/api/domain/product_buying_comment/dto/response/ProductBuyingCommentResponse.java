package me.sallim.api.domain.product_buying_comment.dto.response;

import lombok.Builder;
import me.sallim.api.domain.product_buying_comment.model.ProductBuyingComment;

@Builder
public record ProductBuyingCommentResponse(
        Long commentId,
        Long memberId,
        String content
) {
    public static ProductBuyingCommentResponse from(ProductBuyingComment comment) {
        return new ProductBuyingCommentResponse(
                comment.getId(),
                comment.getMemberId(),
                comment.getContent()
        );
    }
}
