package me.sallim.api.domain.ai_analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
public class AIImageController {

    private final AIImageAnalysisService aiImageAnalysisService;

    @Operation(
            summary = "상품 이미지 AI 분석",
            description = """
            업로드된 상품 이미지를 AI로 분석하여 상품 정보를 자동으로 추출합니다.
            
            ### 요청 형식:
            - 요청은 multipart/form-data 형식으로 보내야 합니다.
            - file: 분석할 이미지 파일
            - applyPreprocessing: 전처리 적용 여부 (선택사항, 기본값: true)
            
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
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        
        AIImageAnalysisResponse response = aiImageAnalysisService.analyzeProductImage(file, applyPreprocessing);
        
        return ResponseEntity.ok(response);
    }
}
