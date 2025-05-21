package me.sallim.api.domain.product_selling_answer.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateProductSellingAnswerRequest(
        List<AnswerDto> answers
) {
    @Builder
    public record AnswerDto(
            Long questionId,
            String content
    ) {}
}
