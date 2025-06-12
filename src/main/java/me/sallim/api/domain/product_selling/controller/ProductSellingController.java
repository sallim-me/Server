package me.sallim.api.domain.product_selling.controller;

import io.swagger.v3.oas.annotations.Operation;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product_selling.dto.request.CreateProductSellingRequest;
import me.sallim.api.domain.product_selling.dto.request.UpdateProductSellingRequest;
import me.sallim.api.domain.product_selling.dto.response.ProductSellingDetailResponse;
import me.sallim.api.domain.product_selling.dto.response.ProductSellingSummaryResponse;
import me.sallim.api.domain.product_selling.service.ProductSellingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/product/selling")
@RequiredArgsConstructor
@Slf4j
public class ProductSellingController {

    private final ProductSellingService productSellingService;

    @Operation(
            summary = "판매 글 작성",
            description = """
        판매 회원이 새로운 판매 글을 작성합니다. 글 작성 시 제품 정보와 고정 질문에 대한 답변도 함께 제출됩니다.

        제품 종류별로 3개의 고정 질문이 db에 존재하며, 이에 대한 답변을 함께 전송해야 합니다.
        
        사진도 함께 업로드할 수 있으며, 사진이 있을 경우 첫 번째 사진이 썸네일로 지정됩니다.

        ### 요청 예시:
        ```json
        {
          "title": "삼성 냉장고 팝니다",
          "content": "삼성 냉장고 RT58K7100BS 팝니다. 상태 양호합니다.",
          "applianceType": "REFRIGERATOR",
          "modelNumber": "RT58K7100BS",
          "modelName": "삼성 냉장고 580L RT58K7100BS",
          "brand": "삼성",
          "price": 800000,
          "userPrice": 750000,
          "answers": [
            {
              "questionId": 1,
              "answerContent": "성에가 잘 생기지 않았습니다."
            },
            {
              "questionId": 2,
              "answerContent": "온도는 대체로 일정했어요."
            },
            {
              "questionId": 3,
              "answerContent": "약간의 소음이 있지만 거슬리지 않았습니다."
            }
          ]
        }
        ```
        """
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductSellingDetailResponse>> createSellingProduct(
            @LoginMember Member loginMember,
            @RequestPart(value = "request") String requestStr,
//            @RequestPart(value = "request") CreateProductSellingRequest request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        try {
            // JSON 문자열을 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CreateProductSellingRequest request = objectMapper.readValue(requestStr, CreateProductSellingRequest.class);

            log.info("판매글 작성 요청 - 사용자: {}, 제목: {}, 사진 개수: {}",
                    loginMember != null ? loginMember.getId() : "비로그인",
                    request.getTitle(),
                    photos != null ? photos.size() : 0);

            // 사진이 null인 경우 빈 리스트로 처리
            List<MultipartFile> safePhotos = photos != null ? photos : Collections.emptyList();

            ProductSellingDetailResponse detail = productSellingService.createSellingProduct(loginMember, request, safePhotos);
            return ResponseEntity.ok(ApiResponse.success(detail));
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("BAD_REQUEST", "요청 형식이 올바르지 않습니다: " + e.getMessage()));
        } catch (Exception e) {
            log.error("판매글 작성 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "판매글 작성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }


    @Operation(
            summary = "판매글 단건 조회",
            description = """
        특정 판매글의 상세 정보를 조회합니다.

        - 제품 정보와 함께, 해당 제품 종류의 고정 질문 목록과 그에 대한 사용자의 답변을 함께 반환합니다.
        - 로그인한 사용자의 경우 본인 작성 여부(isAuthor)를 함께 반환합니다.

        ### Path Variable:
        - `productId`: 조회할 판매글(제품)의 ID

        ### 응답 예시:
        ```json
        {
          "status": 200,
          "code": "SUCCESS",
          "message": "요청이 성공했습니다.",
          "data": {
            "title": "삼성 냉장고 팝니다",
            "content": "삼성 냉장고 RT58K7100BS 팝니다. 상태 양호합니다.",
            "applianceType": "REFRIGERATOR",
            "modelNumber": "RT58K7100BS",
            "modelName": "삼성 냉장고 580L RT58K7100BS",
            "brand": "삼성",
            "price": 800000,
            "userPrice": 750000,
            "isActive": true,
            "isAuthor": true,
            "answers": [
              {
                "questionId": 1,
                "questionContent": "냉동실 내부에 성에가 과도하게 생기거나 물이 새는 현상이 있었나요?",
                "answerContent": "성에가 잘 생기지 않았습니다."
              },
              ...
            ]
          }
        }
        ```
        """
    )
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductSellingDetailResponse>> getProductSellingDetail(
            @PathVariable Long productId,
            @LoginMember Member member) {
        ProductSellingDetailResponse detail = productSellingService.getProductSellingDetail(productId, member);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @Operation(
            summary = "판매글 수정",
            description = """
        본인이 작성한 판매글을 수정합니다.

        - 제목, 내용, 제품 정보(모델명/번호, 브랜드, 가격 등), 그리고 고정 질문에 대한 답변을 수정할 수 있습니다.
        - 제품 종류가 변경되면 기존 질문-답변은 삭제되고, 새로운 질문에 대한 답변을 다시 작성해야 합니다.
        - `isActive` 값을 함께 전송하면 게시글 활성화 여부도 수정됩니다.

        ### 요청 예시:
        ```json
        {
          "title": "LG 세탁기 팝니다",
          "content": "상태 좋은 세탁기 판매해요",
          "applianceType": "WASHING_MACHINE",
          "isActive": false,
          "modelName": "LG 트롬 21kg F21WKT",
          "modelNumber": "F21WKT",
          "brand": "LG",
          "price": 700000,
          "userPrice": 650000,
          "answers": [
            {
              "questionId": 4,
              "answerContent": "진동이 살짝 있었지만 문제 없었습니다."
            },
            ...
          ]
        }
        ```
        """
    )
    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductSellingDetailResponse>> updateSellingProduct(
            @LoginMember Member loginMember,
            @PathVariable Long productId,
            @RequestBody UpdateProductSellingRequest request) {
        ProductSellingDetailResponse updated = productSellingService.updateSellingProduct(loginMember, productId, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(
            summary = "판매글 삭제",
            description = "본인이 작성한 판매글을 완전히 삭제합니다."
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteSellingProduct(
            @LoginMember Member loginMember,
            @PathVariable Long productId) {
        productSellingService.deleteSellingProduct(loginMember, productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
