package me.sallim.api.domain.product_selling_answer.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.appliance_type_question.repository.ApplianceTypeQuestionRepository;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.repository.ProductRepository;
import me.sallim.api.domain.product_selling_answer.dto.request.CreateProductSellingAnswerRequest;
import me.sallim.api.domain.product_selling_answer.dto.response.ProductSellingAnswerResponse;
import me.sallim.api.domain.product_selling_answer.model.ProductSellingAnswer;
import me.sallim.api.domain.product_selling_answer.repository.ProductSellingAnswerRepository;
import me.sallim.api.domain.appliance_type_question.model.ApplianceTypeQuestion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSellingAnswerService {

    private final ProductSellingAnswerRepository answerRepository;
    private final ProductRepository productRepository;
    private final ApplianceTypeQuestionRepository questionRepository;

    @Transactional
    public void saveAnswers(Long productId, CreateProductSellingAnswerRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));

        List<ProductSellingAnswer> answers = request.answers().stream()
                .map(dto -> {
                    ApplianceTypeQuestion question = questionRepository.findById(dto.questionId())
                            .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다."));

                    if (!question.getApplianceType().equals(product.getApplianceType())) {
                        throw new IllegalArgumentException("제품 종류와 질문이 일치하지 않습니다. questionId = " + dto.questionId());
                    }
                    return ProductSellingAnswer.builder()
                            .product(product)
                            .question(question)
                            .content(dto.content())
                            .build();
                })
                .toList();

        answerRepository.saveAll(answers);
    }

    @Transactional(readOnly = true)
    public List<ProductSellingAnswerResponse> getAnswersByProduct(Long productId) {
        List<ProductSellingAnswer> answers = answerRepository.findByProductId(productId);
        return answers.stream()
                .map(ProductSellingAnswerResponse::from)
                .toList();
    }
}
