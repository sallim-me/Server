package me.sallim.api;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import java.util.Iterator;

public class WebPLibraryTest {
    
    private static final Logger log = LoggerFactory.getLogger(WebPLibraryTest.class);
    
    @Test
    public void testWebPLibraryAvailability() {
        log.info("=== WebP 라이브러리 테스트 시작 ===");
        
        // 시스템 정보
        String systemArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        log.info("운영체제: {} ({})", osName, systemArch);
        
        // 클래스패스 확인
        String classPath = System.getProperty("java.class.path");
        log.info("TwelveMonkeys 라이브러리 클래스패스 확인:");
        String[] paths = classPath.split(System.getProperty("path.separator"));
        boolean foundWebpJar = false;
        for (String path : paths) {
            if (path.contains("imageio-webp") || path.contains("twelvemonkeys")) {
                log.info("  ✅ 발견: {}", path);
                foundWebpJar = true;
            }
        }
        if (!foundWebpJar) {
            log.warn("❌ 클래스패스에서 TwelveMonkeys WebP JAR 파일을 찾을 수 없습니다");
        }
        
        // 클래스 직접 로드 테스트
        log.info("=== 클래스 직접 로드 테스트 ===");
        try {
            Class<?> webpWriterClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageWriter");
            log.info("✅ WebPImageWriter 클래스 로드 성공: {}", webpWriterClass.getName());
            
            Class<?> webpReaderClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageReader");
            log.info("✅ WebPImageReader 클래스 로드 성공: {}", webpReaderClass.getName());
            
            Class<?> webpProviderClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageWriterSpi");
            log.info("✅ WebPImageWriterSpi 클래스 로드 성공: {}", webpProviderClass.getName());
            
        } catch (ClassNotFoundException e) {
            log.error("❌ TwelveMonkeys WebP 클래스 로드 실패: {}", e.getMessage());
        }
        
        // ImageIO Service Provider 확인
        log.info("=== ImageIO Service Provider 확인 ===");
        IIORegistry registry = IIORegistry.getDefaultInstance();
        Iterator<ImageWriterSpi> writerSpis = registry.getServiceProviders(ImageWriterSpi.class, true);
        boolean foundWebpSpi = false;
        while (writerSpis.hasNext()) {
            ImageWriterSpi spi = writerSpis.next();
            String[] formatNames = spi.getFormatNames();
            for (String format : formatNames) {
                if ("webp".equalsIgnoreCase(format)) {
                    log.info("✅ WebP ImageWriterSpi 발견: {} (포맷: {})", spi.getClass().getName(), String.join(", ", formatNames));
                    foundWebpSpi = true;
                }
            }
        }
        if (!foundWebpSpi) {
            log.warn("❌ WebP ImageWriterSpi를 찾을 수 없습니다");
        }
        
        // ImageIO.getImageWritersByFormatName 테스트
        log.info("=== ImageIO.getImageWritersByFormatName 테스트 ===");
        Iterator<ImageWriter> webpWriters = ImageIO.getImageWritersByFormatName("webp");
        if (webpWriters.hasNext()) {
            ImageWriter writer = webpWriters.next();
            log.info("✅ WebP ImageWriter 발견: {}", writer.getClass().getName());
            writer.dispose();
        } else {
            log.warn("❌ ImageIO.getImageWritersByFormatName(\"webp\")로 WebP Writer를 찾을 수 없습니다");
        }
        
        // 사용 가능한 모든 포맷 출력
        String[] writerNames = ImageIO.getWriterFormatNames();
        log.info("사용 가능한 ImageWriter 포맷: {}", String.join(", ", writerNames));
        
        log.info("=== WebP 라이브러리 테스트 완료 ===");
    }
}
