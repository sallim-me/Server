package me.sallim.api.global.config;

import lombok.extern.slf4j.Slf4j;
import me.sallim.api.common.util.ImageConverter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 WebP 라이브러리 상태를 확인하는 컴포넌트
 */
@Component
@Slf4j
public class WebPLibraryChecker implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== 애플리케이션 시작 시 WebP 라이브러리 상태 확인 ===");
        
        try {
            boolean isAvailable = ImageConverter.isWebPLibraryAvailable();
            
            if (isAvailable) {
                log.info("✅ WebP 라이브러리가 정상적으로 로드되었습니다.");
            } else {
                log.warn("❌ WebP 라이브러리를 사용할 수 없습니다. 이미지는 원본 형식으로 저장됩니다.");
            }
        } catch (Exception e) {
            log.error("WebP 라이브러리 상태 확인 중 오류 발생: {}", e.getMessage(), e);
        }
        
        log.info("=== WebP 라이브러리 상태 확인 완료 ===");
    }
}
