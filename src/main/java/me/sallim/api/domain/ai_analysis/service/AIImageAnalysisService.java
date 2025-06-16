package me.sallim.api.domain.ai_analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sallim.api.common.util.ImageConverter;
import me.sallim.api.domain.ai_analysis.dto.response.AIImageAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIImageAnalysisService {
    
    @Value("${spring.fastapi.url}")
    private String fastApiUrl;
    
    private final RestTemplate restTemplate;
    
    public AIImageAnalysisResponse analyzeProductImage(MultipartFile file, boolean applyPreprocessing) {
        try {
            String url = fastApiUrl + "/generate-listing?apply_preprocessing=" + applyPreprocessing;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // WebP 파일인 경우 PNG로 변환
            MultipartFile processedFile = preprocessImageFile(file);
            body.add("file", processedFile.getResource());
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            ResponseEntity<AIImageAnalysisResponse> response = restTemplate.postForEntity(
                url, requestEntity, AIImageAnalysisResponse.class);
            
            if (response.getBody() != null && Boolean.TRUE.equals(response.getBody().getSuccess())) {
                log.info("AI 이미지 분석 성공: {}", response.getBody().getTitle());
                return response.getBody();
            } else {
                log.error("AI 분석이 실패했습니다. 응답: {}", response.getBody());
                throw new RuntimeException("AI 분석이 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("Failed to analyze image with AI service", e);
            throw new RuntimeException("AI 이미지 분석에 실패했습니다: " + e.getMessage());
        }
    }
    
    private MultipartFile preprocessImageFile(MultipartFile file) {
        try {
            if (ImageConverter.isWebPFile(file)) {
                log.info("WebP 파일 감지, PNG로 변환 중: {}", file.getOriginalFilename());
                MultipartFile convertedFile = ImageConverter.convertWebPToPng(file);
                log.info("WebP 변환 성공: {} -> {}", file.getOriginalFilename(), convertedFile.getOriginalFilename());
                return convertedFile;
            }
            
            return file;
        } catch (Exception e) {
            log.error("이미지 전처리 실패: {}", e.getMessage(), e);
            // WebP 파일이면서 변환이 실패한 경우 에러를 던짐 (FastAPI로 WebP 파일을 보내면 안됨)
            if (ImageConverter.isWebPFile(file)) {
                throw new RuntimeException("WebP 파일 변환이 실패했습니다. 다른 형식의 이미지를 사용해주세요: " + e.getMessage());
            }
            // WebP가 아닌 다른 파일은 원본 사용
            log.warn("비WebP 파일 전처리 실패, 원본 파일 사용: {}", e.getMessage());
            return file;
        }
    }
}
