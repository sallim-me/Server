package me.sallim.api.domain.product_comment.dto.response;

import lombok.Builder;
import me.sallim.api.domain.product_comment.model.ProductComment;

@Builder
public record ProductCommentResponse(
        Long commentId,
        Long memberId,
        String nickname,
        String content
) {
    public static ProductCommentResponse from(ProductComment comment) {
        return new ProductCommentResponse(
                comment.getId(),
                comment.getMember().getId(),
                comment.getMember().getNickname(),
                comment.getContent()
        );
    }
}
