package me.sallim.api.domain.product_scrap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product_scrap.dto.request.ScrapRequest;
import me.sallim.api.domain.product_scrap.dto.response.ScrapListResponse;
import me.sallim.api.domain.product_scrap.dto.response.ScrapResponse;
import me.sallim.api.domain.product_scrap.service.ScrapService;
import me.sallim.api.global.annotation.LoginMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scrap")
@RequiredArgsConstructor
@Tag(name = "Product Scrap", description = "상품 스크랩 관련 API")
public class ScrapController {

    private final ScrapService scrapService;

    @Operation(summary = "상품 스크랩 생성", description = """
        사용자가 관심있는 상품을 스크랩합니다.
        
        ### 요청 예시:
        ```json
        {
          "productId": 123,
          "memo": "나중에 문의해볼 상품"
        }
        ```
        
        ### 응답:
        - 201 Created: 스크랩 생성 성공
        - 400 Bad Request: 유효하지 않은 요청 (이미 스크랩한 상품 등)
        - 404 Not Found: 존재하지 않는 상품
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "스크랩 생성 성공"),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 요청"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @PostMapping
    public ResponseEntity<ScrapResponse> createScrap(
            @Parameter(description = "로그인된 사용자 정보", required = true)
            @LoginMember Member member,
            @Parameter(description = "스크랩 생성 정보", required = true, schema = @Schema(implementation = ScrapRequest.class))
            @Valid @RequestBody ScrapRequest requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scrapService.createScrap(member.getId(), requestDto));
    }

    @Operation(summary = "스크랩 목록 조회", description = """
        사용자의 스크랩 목록을 페이지네이션하여 조회합니다.
        
        ### 요청 파라미터:
        - page: 페이지 번호 (0부터 시작, 기본값 0)
        - size: 페이지 크기 (기본값 10)
        - sort: 정렬 기준 (기본값 createdAt,desc)
        
        ### 응답:
        - 200 OK: 스크랩 목록 조회 성공
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "스크랩 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<ScrapListResponse> getScrapsByMember(
//            @Parameter(description = "로그인된 사용자 정보", required = true)
            @LoginMember Member member,
            @Parameter(description = "페이지네이션 정보")
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(scrapService.getScrapsByMemberId(member.getId(), pageable));
    }

    @Operation(summary = "스크랩 삭제", description = """
        사용자의 스크랩을 삭제합니다.
        
        ### Path Variable:
        - scrapId: 삭제할 스크랩 ID
        
        ### 응답:
        - 204 No Content: 스크랩 삭제 성공
        - 403 Forbidden: 권한 없음 (다른 사용자의 스크랩)
        - 404 Not Found: 존재하지 않는 스크랩
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "스크랩 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 스크랩")
    })
    @DeleteMapping("/{scrapId}")
    public ResponseEntity<Void> deleteScrap(
            @Parameter(description = "삭제할 스크랩 ID", required = true)
            @PathVariable Long scrapId,
            @Parameter(description = "로그인된 사용자 정보", required = true)
            @LoginMember Member member) {
        scrapService.deleteScrap(scrapId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "스크랩 메모 수정", description = """
        스크랩에 추가한 메모를 수정합니다.
        
        ### Path Variable:
        - scrapId: 수정할 스크랩 ID
        
        ### 요청 바디:
        - 메모 내용 (텍스트)
        
        ### 응답:
        - 200 OK: 메모 수정 성공
        - 403 Forbidden: 권한 없음 (다른 사용자의 스크랩)
        - 404 Not Found: 존재하지 않는 스크랩
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메모 수정 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 스크랩")
    })
    @PatchMapping("/{scrapId}/memo")
    public ResponseEntity<ScrapResponse> updateScrapMemo(
            @Parameter(description = "수정할 스크랩 ID", required = true)
            @PathVariable Long scrapId,
            @Parameter(description = "로그인된 사용자 정보", required = true)
            @LoginMember Member member,
            @Parameter(description = "수정할 메모 내용", required = true)
            @RequestBody String memo) {
        return ResponseEntity.ok(scrapService.updateScrapMemo(scrapId, member.getId(), memo));
    }

    @Operation(summary = "스크랩 여부 확인", description = """
        사용자가 특정 상품을 스크랩했는지 여부를 확인합니다.
        
        ### Path Variable:
        - productId: 확인할 상품 ID
        
        ### 응답:
        - 200 OK: 스크랩 여부 반환 (true/false)
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "스크랩 여부 확인 성공")
    })
    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> isProductScrapped(
            @Parameter(description = "확인할 상품 ID", required = true)
            @PathVariable Long productId,
            @Parameter(description = "로그인된 사용자 정보", required = true)
            @LoginMember Member member) {
        return ResponseEntity.ok(scrapService.isProductScrappedByMember(productId, member.getId()));
    }

    @Operation(summary = "상품 스크랩 수 조회", description = """
        특정 상품의 총 스크랩 수를 조회합니다.
        
        ### Path Variable:
        - productId: 조회할 상품 ID
        
        ### 응답:
        - 200 OK: 스크랩 수 반환 (숫자)
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "스크랩 수 조회 성공")
    })
    @GetMapping("/count/{productId}")
    public ResponseEntity<Long> getScrapCount(
            @Parameter(description = "조회할 상품 ID", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(scrapService.getScrapCountByProduct(productId));
    }
}
