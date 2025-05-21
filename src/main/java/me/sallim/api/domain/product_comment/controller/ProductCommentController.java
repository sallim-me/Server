package me.sallim.api.domain.product_comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product_comment.dto.request.CreateProductCommentRequest;
import me.sallim.api.domain.product_comment.dto.response.ProductCommentResponse;
import me.sallim.api.domain.product_comment.service.ProductCommentService;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/comment/")
@RequiredArgsConstructor
public class ProductCommentController {

    private final ProductCommentService commentService;

    @Operation(
            summary = "댓글 작성",
            description = """
            판매글 또는 구매글에 댓글을 작성합니다.

            ### Path Variable:
            - `productId` : 댓글을 작성할 게시글의 ID

            ### 요청 예시:
            ```json
            {
              "content": "제품에 대해 궁금한 게 있어요!"
            }
            ```
            """
    )
    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductCommentResponse>> createComment(
            @LoginMember Member loginMember,
            @PathVariable Long productId,
            @RequestBody CreateProductCommentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(commentService.createComment(loginMember, productId, request)));
    }

    @Operation(
            summary = "댓글 조회",
            description = """
            특정 판매글 또는 구매글의 댓글을 조회합니다.

            ### Path Variable:
            - `productId`: 조회할 게시글의 ID

            ### 요청 예시:
            `/product/3/comments`
            """
    )
    @GetMapping("/{productId}/comments")
    public ResponseEntity<ApiResponse<List<ProductCommentResponse>>> getComments(
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getComments(productId)));
    }

    @Operation(
            summary = "댓글 삭제",
            description = """
            본인이 작성한 댓글만 삭제할 수 있습니다.

            ### Path Variable:
            - `commentId`: 삭제할 댓글의 ID
            """
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @LoginMember Member loginMember,
            @PathVariable Long commentId) {
        commentService.deleteComment(loginMember, commentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}