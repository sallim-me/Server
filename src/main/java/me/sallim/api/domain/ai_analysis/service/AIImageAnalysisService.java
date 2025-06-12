package me.sallim.api.domain.ai_analysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            body.add("file", file.getResource());
            
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
}
