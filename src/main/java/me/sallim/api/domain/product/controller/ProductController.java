package me.sallim.api.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.dto.ProductListResponse;
import me.sallim.api.domain.product.service.ProductService;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "Product", description = "상품 관련 API")
public class ProductController {
    
    private final ProductService productService;
    
    @Operation(
            summary = "전체 상품 목록 조회",
            description = """
            모든 상품(판매글 + 구매글)을 통합하여 조회합니다.
            현재는 Product 테이블만 사용하여 기본 정보를 제공합니다.
            로그인한 사용자의 경우 스크랩 여부와 본인 작성 여부도 함께 반환됩니다.
            
            ### 응답 필드:
            - id: 상품 ID
            - title: 제목
            - tradeType: 거래 타입 ("SELLING" 또는 "BUYING")
            - category: 가전 타입 (REFRIGERATOR, WASHING_MACHINE, AIR_CONDITIONER)
            - modelName: 모델명 (현재는 빈 문자열)
            - priceOrQuantity: 가격/수량 (현재는 빈 문자열)
            - description: 설명
            - isScraped: 스크랩 여부 (로그인 시에만 true/false, 미로그인 시 false)
            - isAuthor: 작성자 여부 (로그인 시에만 true/false, 미로그인 시 false)
            - createdAt: 작성일시
            
            ### 응답 예시:
            ```json
            {
              "status": 200,
              "code": "SUCCESS",
              "message": "요청이 성공했습니다.",
              "data": [
                {
                  "id": 12,
                  "title": "삼성 냉장고 팝니다",
                  "tradeType": "SELLING",
                  "category": "REFRIGERATOR",
                  "modelName": "",
                  "priceOrQuantity": "",
                  "description": "거의 새 제품입니다",
                  "isScraped": false,
                  "isAuthor": true,
                  "createdAt": "2024-05-01T14:30:00"
                },
                {
                  "id": 7,
                  "title": "세탁기 구매 희망",
                  "tradeType": "BUYING",
                  "category": "WASHING_MACHINE",
                  "modelName": "",
                  "priceOrQuantity": "",
                  "description": "급하게 구매하고 싶습니다",
                  "isScraped": true,
                  "isAuthor": false,
                  "createdAt": "2024-04-28T11:10:00"
                }
              ]
            }
            ```
            """
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ProductListResponse>>> getAllProducts(
            @Parameter(description = "로그인된 사용자 정보 (선택사항)")
            @LoginMember Member member) {
        
        List<ProductListResponse> products = productService.getAllProducts(member);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
