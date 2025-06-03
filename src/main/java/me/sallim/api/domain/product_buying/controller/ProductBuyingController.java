package me.sallim.api.domain.product_buying.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;

import me.sallim.api.domain.product.repository.ProductRepository;
import me.sallim.api.domain.product_buying.dto.request.CreateProductBuyingRequest;
import me.sallim.api.domain.product_buying.dto.request.UpdateProductBuyingRequest;
import me.sallim.api.domain.product_buying.dto.response.ProductBuyingDetailResponse;
import me.sallim.api.domain.product_buying.service.ProductBuyingService;
import me.sallim.api.domain.product_comment.dto.request.CreateProductCommentRequest;
import me.sallim.api.domain.product_comment.dto.response.ProductCommentResponse;
import me.sallim.api.domain.product_comment.service.ProductCommentService;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.sallim.api.domain.product_buying.model.QProductBuying.productBuying;

@RestController
@RequestMapping("/product/buying")
@RequiredArgsConstructor
public class ProductBuyingController {

    private final ProductBuyingService productBuyingService;
    private final ProductRepository productRepository;
    private final ProductCommentService productCommentService;

    @Operation(summary = "구매 글 작성", description = """
        바이어 회원만 구매 글을 작성할 수 있습니다.
        구매 수량은 최소 3개 이상이어야 합니다.

        ### 요청 예시:
        ```json
        {
          "title": "냉장고 대량 구매",
          "content": "삼성 냉장고 10대 필요",
          "quantity": 10,
          "applianceType": "REFRIGERATOR",
          "price": 300000
        }
        ```
        """)
    @PostMapping
    public ResponseEntity<ApiResponse<ProductBuyingDetailResponse>> createProductBuying(@LoginMember Member loginMember,
                                                                                        @RequestBody CreateProductBuyingRequest request) {
        ProductBuyingDetailResponse createdDetail = productBuyingService.createProductBuying(loginMember, request);
        return ResponseEntity.ok(ApiResponse.success(createdDetail));
    }

    @Operation(summary = "구매 글 단건 조회", description = """
        특정 구매 글의 상세 내용을 조회합니다.

        ### Path Variable
        - productId: 조회할 글 ID
        """)
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductBuyingDetailResponse>> getProductBuying(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(productBuyingService.getProductBuyingDetail(productId)));
    }


    @Operation(summary = "구매 글 수정", description = """
        구매 글의 제목, 내용, 수량, 가격, 가전 타입을 수정합니다.
        구매 수량은 최소 3개 이상이어야 합니다.

        ### 요청 예시:
        ```json
        {
          "title": "새로운 냉장고 구매",
          "content": "LG 냉장고 5대 필요",
          "quantity": 5,
          "applianceType": "REFRIGERATOR",
          "price": 250000
        }
        ```
        """)
    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductBuyingDetailResponse>> updateProductBuying(
            @LoginMember Member loginMember,
            @PathVariable Long productId,
            @RequestBody UpdateProductBuyingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productBuyingService.updateProductBuying(loginMember, productId, request)));
    }

    @Operation(summary = "구매 글 삭제", description = """
        특정 구매 글을 삭제합니다.

        ### Path Variable
        - productId: 삭제할 글 ID (작성자 본인만 삭제 가능)
        """)
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProductBuying(
            @LoginMember Member loginMember,
            @PathVariable Long productId) {
        productBuyingService.deleteProductBuying(loginMember, productId);
        return ResponseEntity.ok(ApiResponse.success("삭제 완료"));
    }
}
