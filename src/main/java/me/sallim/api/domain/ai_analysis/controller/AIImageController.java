package me.sallim.api.domain.ai_analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sallim.api.domain.ai_analysis.dto.response.AIImageAnalysisResponse;
import me.sallim.api.domain.ai_analysis.service.AIImageAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
@Tag(name = "AI Image Analysis", description = "AI 이미지 분석 API")
@Slf4j
public class AIImageController {

    private final AIImageAnalysisService aiImageAnalysisService;

    @Operation(
            summary = "상품 이미지 AI 분석",
            description = """
            업로드된 상품 이미지를 AI로 분석하여 상품 정보를 자동으로 추출합니다.
            
            ### 요청 형식:
            - 요청은 multipart/form-data 형식으로 보내야 합니다.
            - file: 분석할 이미지 파일 (JPG, PNG, GIF, BMP, WebP, TIFF 지원)
            - applyPreprocessing: 전처리 적용 여부 (선택사항, 기본값: true)
            
            ### WebP 파일 처리:
            - WebP 파일은 자동으로 PNG 형식으로 변환되어 처리됩니다.
            - 변환 과정에서 투명도와 품질을 보존합니다.
            
            ### 응답:
            - 상품명, 카테고리, 모델코드, 브랜드, 예상가격, 설명 등
            
            ### 응답 예시:
            ```json
            {
              "title": "[삼성] RT17FARAEWW 냉장고 판매합니다.",
              "category": "REFRIGERATOR",
              "modelCode": "RT17FARAEWW",
              "brand": "samsung",
              "price": 300000,
              "description": "안녕하세요! 삼성 RT17FARAEWW 냉장고를 저렴하게 판매합니다...",
              "processingTime": 4.123379945755005,
              "success": true
            }
            ```
            """
    )
    @PostMapping(value = "/analyze-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AIImageAnalysisResponse> analyzeImage(
            @Parameter(description = "분석할 이미지 파일") @RequestPart("file") MultipartFile file,
            @Parameter(description = "전처리 적용 여부") @RequestParam(defaultValue = "true") boolean applyPreprocessing) {
        
        // 파일 유효성 검사
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어있습니다.");
        }
        
        // 이미지 파일 타입 검사
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        // MIME 타입과 파일 확장자를 모두 확인
        boolean isValidImage = false;
        
        // MIME 타입 검사
        if (contentType != null && contentType.startsWith("image/")) {
            isValidImage = true;
        }
        
        // 파일 확장자 검사 (MIME 타입이 없거나 잘못된 경우 대비)
        if (!isValidImage && originalFilename != null) {
            String extension = originalFilename.toLowerCase();
            if (extension.endsWith(".jpg") || extension.endsWith(".jpeg") || 
                extension.endsWith(".png") || extension.endsWith(".gif") || 
                extension.endsWith(".bmp") || extension.endsWith(".webp") ||
                extension.endsWith(".tiff") || extension.endsWith(".tif")) {
                isValidImage = true;
                // webp 파일의 경우 MIME 타입이 누락되었을 때 수동 설정
                if (extension.endsWith(".webp") && contentType == null) {
                    // MultipartFile의 contentType은 수정할 수 없으므로 로그만 남김
                    log.warn("WebP 파일의 MIME 타입이 누락되었습니다: {}", originalFilename);
                }
            }
        }
        
        if (!isValidImage) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다. 지원 형식: JPG, PNG, GIF, BMP, WebP, TIFF");
        }
        
        try {
            AIImageAnalysisResponse response = aiImageAnalysisService.analyzeProductImage(file, applyPreprocessing);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("파일 검증 실패: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("AI 이미지 분석 중 런타임 에러: {}", e.getMessage());
            if (e.getMessage().contains("WebP 파일 변환이 실패했습니다")) {
                throw new IllegalArgumentException("WebP 파일 처리에 실패했습니다. JPG, PNG 등 다른 형식의 이미지를 사용해주세요.");
            }
            throw e;
        } catch (Exception e) {
            log.error("AI 이미지 분석 중 예상치 못한 에러: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
