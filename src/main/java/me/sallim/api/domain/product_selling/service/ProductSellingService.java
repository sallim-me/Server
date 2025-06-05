package me.sallim.api.domain.product_selling.service;

import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product_selling.dto.request.UpdateProductSellingRequest;
import org.springframework.transaction.annotation.Transactional;
import me.sallim.api.domain.appliance_type_question.repository.ApplianceTypeQuestionRepository;
import me.sallim.api.domain.product_selling.dto.request.CreateProductSellingRequest;
import me.sallim.api.domain.product_selling.dto.response.ProductSellingDetailResponse;
import me.sallim.api.domain.product_selling.dto.response.ProductSellingSummaryResponse;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product_selling.model.ProductSelling;
import me.sallim.api.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.product_selling.repository.ProductSellingQueryRepository;
import me.sallim.api.domain.product_selling.repository.ProductSellingRepository;
import me.sallim.api.domain.product_selling_answer.model.ProductSellingAnswer;
import me.sallim.api.domain.appliance_type_question.model.ApplianceTypeQuestion;
import me.sallim.api.domain.product_selling_answer.repository.ProductSellingAnswerRepository;
import me.sallim.api.domain.product_selling_answer.dto.response.ProductSellingAnswerResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductSellingService {

    private final ProductRepository productRepository;
    private final ProductSellingRepository productSellingRepository;
    private final ProductSellingQueryRepository productSellingQueryRepository;
    private final ApplianceTypeQuestionRepository applianceTypeQuestionRepository;
    private final ProductSellingAnswerRepository productSellingAnswerRepository;
    private final ApplianceTypeQuestionRepository questionRepository;

    @Transactional
    public ProductSellingDetailResponse createSellingProduct(Member member, CreateProductSellingRequest request) {
        // 1. Product 저장
        Product product = Product.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .applianceType(request.getApplianceType())
                .postType(PostTypeEnum.SELLING)
                .isActive(true)
                .build();
        productRepository.save(product);

        // 2. ProductSelling 저장
        ProductSelling selling = ProductSelling.builder()
                .product(product)
                .modelName(request.getModelName())
                .modelNumber(request.getModelNumber())
                .brand(request.getBrand())
                .price(request.getPrice())
                .userPrice(request.getUserPrice())
                .build();
        productSellingRepository.save(selling);

        // 3. 질문-답변 저장
        List<ApplianceTypeQuestion> questions = applianceTypeQuestionRepository.findByApplianceType(request.getApplianceType());
        List<ProductSellingAnswer> answers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            answers.add(ProductSellingAnswer.builder()
                    .product(product)
                    .question(questions.get(i))
                    .content(request.getAnswers().get(i).getAnswerContent())
                    .build());
        }
        productSellingAnswerRepository.saveAll(answers);

        // 4. 응답 DTO 반환
        return ProductSellingDetailResponse.from(selling, product, answers);
    }

    @Transactional(readOnly = true)
    public ProductSellingDetailResponse getProductSellingDetail(Long productId, Member currentMember) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제품이 존재하지 않습니다."));

        ProductSelling selling = productSellingRepository.findByProduct(product)
                .orElseThrow(() -> new IllegalArgumentException("판매 정보가 없습니다."));

        List<ProductSellingAnswer> answers = productSellingAnswerRepository.findByProduct(product);

        boolean isAuthor = false;
        if (currentMember != null) {
            isAuthor = product.getMember().getId().equals(currentMember.getId());
        }

        return ProductSellingDetailResponse.builder()
                .title(product.getTitle())
                .content(product.getContent())
                .isActive(product.getIsActive())
                .applianceType(product.getApplianceType())
                .modelName(selling.getModelName())
                .modelNumber(selling.getModelNumber())
                .brand(selling.getBrand())
                .price(selling.getPrice())
                .userPrice(selling.getUserPrice())
                .answers(answers.stream().map(ProductSellingAnswerResponse::from).toList())
                .isAuthor(isAuthor)
                .build();
    }

    @Transactional
    public ProductSellingDetailResponse updateSellingProduct(Member loginMember, Long productId, UpdateProductSellingRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제품이 존재하지 않습니다."));

        if (!product.getMember().getId().equals(loginMember.getId())) {
            throw new IllegalArgumentException("본인이 작성한 글만 수정할 수 있습니다.");
        }

        ApplianceType oldType = product.getApplianceType();
        ApplianceType newType = request.getApplianceType();

        // isActive가 null이 아닌 경우에만 업데이트
        if (request.getIsActive() != null) {
            product.updateProductInfo(
                request.getTitle(),
                request.getContent(),
                newType,
                request.getIsActive()
            );
        } else {
            // isActive 필드를 기존 값으로 유지
            product.updateProductInfo(
                request.getTitle(),
                request.getContent(),
                newType,
                product.getIsActive()
            );
        }

        ProductSelling selling = productSellingRepository.findByProduct(product)
                .orElseThrow(() -> new IllegalArgumentException("판매 정보가 존재하지 않습니다."));
        selling.updateSellingInfo(
                request.getModelName(),
                request.getModelNumber(),
                request.getBrand(),
                request.getPrice(),
                request.getUserPrice()
        );

        List<ProductSellingAnswer> updatedAnswers;

        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            if (!oldType.equals(newType)) {
                productSellingAnswerRepository.deleteByProduct(product);
                List<ApplianceTypeQuestion> questions = questionRepository.findByApplianceType(newType);
                if (questions.size() != request.getAnswers().size()) {
                    throw new IllegalArgumentException("답변 수와 고정 질문 수가 일치하지 않습니다.");
                }

                updatedAnswers = IntStream.range(0, questions.size())
                        .mapToObj(i -> ProductSellingAnswer.builder()
                                .product(product)
                                .question(questions.get(i))
                                .content(request.getAnswers().get(i).getAnswerContent())
                                .build())
                        .toList();
                productSellingAnswerRepository.saveAll(updatedAnswers);
            } else {
                List<ProductSellingAnswer> existingAnswers = productSellingAnswerRepository.findByProduct(product);
                Map<Long, ProductSellingAnswer> existingMap = existingAnswers.stream()
                        .collect(Collectors.toMap(ans -> ans.getQuestion().getId(), ans -> ans));

                request.getAnswers().forEach(reqAnswer -> {
                    ProductSellingAnswer matched = existingMap.get(reqAnswer.getQuestionId());
                    if (matched == null) {
                        throw new IllegalArgumentException("기존 답변 중 일치하는 질문이 없습니다. questionId=" + reqAnswer.getQuestionId());
                    }
                    matched.updateAnswerContent(reqAnswer.getAnswerContent());
                });

                updatedAnswers = existingAnswers;
            }
        } else {
            updatedAnswers = productSellingAnswerRepository.findByProduct(product);
        }

        return ProductSellingDetailResponse.from(selling, product, updatedAnswers);
    }

    @Transactional
    public void deleteSellingProduct(Member loginMember, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제품이 존재하지 않습니다."));

        if (!product.getMember().getId().equals(loginMember.getId())) {
            throw new IllegalArgumentException("본인이 작성한 글만 삭제할 수 있습니다.");
        }

        // 1. 답변 먼저 삭제
        productSellingAnswerRepository.deleteByProduct(product);

        // 2. 판매 정보 삭제
        ProductSelling selling = productSellingRepository.findByProduct(product)
                .orElseThrow(() -> new IllegalArgumentException("판매 정보가 존재하지 않습니다."));
        productSellingRepository.delete(selling);

        // 3. 제품 자체 삭제
        productRepository.delete(product);
    }
}
