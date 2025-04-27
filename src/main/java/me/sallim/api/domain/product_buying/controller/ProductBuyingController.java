package me.sallim.api.domain.product_buying.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;

import me.sallim.api.domain.product_buying.dto.request.CreateProductBuyingRequest;
import me.sallim.api.domain.product_buying.dto.request.UpdateProductBuyingRequest;
import me.sallim.api.domain.product_buying.dto.response.ProductBuyingDetailResponse;
import me.sallim.api.domain.product_buying.service.ProductBuyingService;
import me.sallim.api.domain.product_buying_comment.dto.request.CreateProductBuyingCommentRequest;
import me.sallim.api.domain.product_buying_comment.dto.response.ProductBuyingCommentResponse;
import me.sallim.api.domain.product_buying_comment.service.ProductBuyingCommentService;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-buying")
@RequiredArgsConstructor
public class ProductBuyingController {

    private final ProductBuyingService productBuyingService;
    private final ProductBuyingCommentService productBuyingCommentService;

    @Operation(summary = "구매 글 작성", description = """
        바이어 회원만 구매 글을 작성할 수 있습니다.

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
        - productBuyingId: 조회할 구매 글 ID
        """)
    @GetMapping("/{productBuyingId}")
    public ResponseEntity<ApiResponse<ProductBuyingDetailResponse>> getProductBuying(@PathVariable Long productBuyingId) {
        ProductBuyingDetailResponse detail = productBuyingService.getProductBuyingDetail(productBuyingId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @Operation(summary = "구매 글 수정", description = """
        구매 글의 제목, 내용, 수량, 가격, 가전 타입을 수정합니다.

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
    @PatchMapping("/{productBuyingId}")
    public ResponseEntity<ApiResponse<ProductBuyingDetailResponse>> updateProductBuying(@LoginMember Member loginMember,
                                                                                        @PathVariable Long productBuyingId,
                                                                                        @RequestBody UpdateProductBuyingRequest request) {
        ProductBuyingDetailResponse updatedDetail = productBuyingService.updateProductBuying(loginMember, productBuyingId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedDetail));
    }

    @Operation(summary = "구매 글 삭제", description = """
        특정 구매 글을 삭제합니다.

        ### Path Variable
        - productBuyingId: 삭제할 구매 글 ID (작성자 본인만 삭제 가능)
        """)
    @DeleteMapping("/{productBuyingId}")
    public ResponseEntity<ApiResponse<String>> deleteProductBuying(@LoginMember Member loginMember,
                                                                   @PathVariable Long productBuyingId) {
        productBuyingService.deleteProductBuying(loginMember, productBuyingId);
        return ResponseEntity.ok(ApiResponse.success("삭제 완료"));
    }

    // =========================== 구매 글 댓글 ===========================

    @Operation(summary = "구매 글에 댓글 작성", description = """
        특정 구매 글에 댓글을 작성합니다.

        ### 요청 예시:
        ```json
        {
          "content": "구매 조건 협의 가능합니다."
        }
        ```
        """)
    @PostMapping("/{productBuyingId}/comments")
    public ResponseEntity<ApiResponse<String>> createComment(@LoginMember Member loginMember,
                                                             @PathVariable Long productBuyingId,
                                                             @RequestBody CreateProductBuyingCommentRequest request) {
        productBuyingCommentService.createComment(loginMember, productBuyingId, request);
        return ResponseEntity.ok(ApiResponse.success("댓글 작성 완료"));
    }

    @Operation(summary = "구매 글 댓글 조회", description = """
        특정 구매 글에 달린 모든 댓글을 조회합니다.

        ### Path Variable
        - productBuyingId: 조회할 구매 글 ID
        """)
    @GetMapping("/{productBuyingId}/comments")
    public ResponseEntity<ApiResponse<List<ProductBuyingCommentResponse>>> getComments(@PathVariable Long productBuyingId) {
        List<ProductBuyingCommentResponse> comments = productBuyingCommentService.getComments(productBuyingId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @Operation(summary = "구매 글 댓글 삭제", description = """
        특정 댓글을 삭제합니다.

        ### Path Variable
        - commentId: 삭제할 댓글 ID (작성자 본인만 삭제 가능)
        """)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@LoginMember Member loginMember,
                                                             @PathVariable Long commentId) {
        productBuyingCommentService.deleteComment(loginMember, commentId);
        return ResponseEntity.ok(ApiResponse.success("댓글 삭제 완료"));
    }
}