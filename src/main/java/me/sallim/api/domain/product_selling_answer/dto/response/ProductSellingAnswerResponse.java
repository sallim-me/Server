package me.sallim.api.domain.product_selling_answer.dto.response;

import lombok.Builder;
import me.sallim.api.domain.product_selling_answer.model.ProductSellingAnswer;

@Builder
public record ProductSellingAnswerResponse(
        Long id,
        Long questionId,
        String questionContent,
        String answerContent
) {
    public static ProductSellingAnswerResponse from(ProductSellingAnswer answer) {
        return ProductSellingAnswerResponse.builder()
                .id(answer.getId())
                .questionId(answer.getQuestion().getId())
                .questionContent(answer.getQuestion().getQuestionContent())
                .answerContent(answer.getContent())
                .build();
    }
}
