package me.sallim.api.domain.product_selling_answer.controller;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product_selling_answer.dto.request.CreateProductSellingAnswerRequest;
import me.sallim.api.domain.product_selling_answer.dto.response.ProductSellingAnswerResponse;
import me.sallim.api.domain.product_selling_answer.service.ProductSellingAnswerService;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/product-selling/answers")
@RequiredArgsConstructor
public class ProductSellingAnswerController {

    private final ProductSellingAnswerService productSellingAnswerService;

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> saveAnswers(@LoginMember Member loginMember,
                                                         @PathVariable Long productId,
                                                         @RequestBody CreateProductSellingAnswerRequest request) {
        productSellingAnswerService.saveAnswers(productId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{productId}/answers")
    public ResponseEntity<ApiResponse<List<ProductSellingAnswerResponse>>> getAnswers(@PathVariable Long productId) {
        List<ProductSellingAnswerResponse> responses = productSellingAnswerService.getAnswersByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}

