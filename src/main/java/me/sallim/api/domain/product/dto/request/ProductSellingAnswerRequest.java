package me.sallim.api.domain.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSellingAnswerRequest {
    private Long questionId;
    private String answerContent;
}