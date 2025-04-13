package me.sallim.api.domain.product.controller;

import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.dto.request.CreateProductSellingRequest;
import me.sallim.api.domain.product.dto.response.ProductSellingSummaryResponse;
import me.sallim.api.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import me.sallim.api.global.annotation.LoginMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/selling")
    public ResponseEntity<List<ProductSellingSummaryResponse>> getSellingProducts() {
        List<ProductSellingSummaryResponse> summaries = productService.getSellingSummaries();
        return ResponseEntity.ok(summaries);
    }

    @PostMapping("/selling")
    public ResponseEntity<Void> createSellingProduct(@LoginMember Member loginMember,
                                                     @RequestBody CreateProductSellingRequest request) {
        productService.createSellingProduct(loginMember.getId(), request);
        return ResponseEntity.ok().build();
    }
}
