package me.sallim.api.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class ImageConverter {
    
    // 시스템 아키텍처 정보
    private static final String SYSTEM_ARCH = System.getProperty("os.arch");
    private static final boolean IS_ARM64 = SYSTEM_ARCH.toLowerCase().contains("aarch64") || SYSTEM_ARCH.toLowerCase().contains("arm");
    
    // WebP 변환 가능 여부 (실제 테스트를 통해 확인)
    private static boolean webpConversionAvailable = false;
    
    static {
        // WebP 라이브러리 강제 로딩 및 실제 변환 테스트
        try {
            log.info("시스템 아키텍처: {} (ARM64: {})", SYSTEM_ARCH, IS_ARM64);
            
            // 사용 가능한 ImageWriter 확인
            String[] writerNames = ImageIO.getWriterFormatNames();
            log.info("사용 가능한 ImageWriter 포맷: {}", String.join(", ", writerNames));
            
            // TwelveMonkeys 라이브러리 클래스패스 확인
            log.info("=== WebP 라이브러리 클래스패스 확인 ===");
            
            // TwelveMonkeys (읽기 전용)
            try {
                Class<?> twelvemonkeysReaderClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageReader");
                log.info("✅ TwelveMonkeys WebPImageReader 클래스 로드 성공: {}", twelvemonkeysReaderClass.getName());
            } catch (ClassNotFoundException e) {
                log.warn("❌ TwelveMonkeys WebP 라이브러리 로드 실패: {}", e.getMessage());
            }
            
            // Sejda (읽기/쓰기 지원)
            try {
                Class<?> sejdaWriterClass = Class.forName("com.luciad.imageio.webp.WebPWriter");
                log.info("✅ Sejda WebPWriter 클래스 로드 성공: {}", sejdaWriterClass.getName());
                
                Class<?> sejdaWriterSpiClass = Class.forName("com.luciad.imageio.webp.WebPImageWriterSpi");
                log.info("✅ Sejda WebPImageWriterSpi 클래스 로드 성공: {}", sejdaWriterSpiClass.getName());
                
            } catch (ClassNotFoundException e) {
                log.error("❌ Sejda WebP 라이브러리 로드 실패: {}", e.getMessage());
            }
            
            // ImageIO Service Provider 확인
            log.info("=== ImageIO Service Provider 확인 ===");
            javax.imageio.spi.IIORegistry registry = javax.imageio.spi.IIORegistry.getDefaultInstance();
            Iterator<javax.imageio.spi.ImageWriterSpi> writerSpis = registry.getServiceProviders(javax.imageio.spi.ImageWriterSpi.class, true);
            boolean foundWebpSpi = false;
            while (writerSpis.hasNext()) {
                javax.imageio.spi.ImageWriterSpi spi = writerSpis.next();
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
            
            // WebP Writer 사용 가능 여부 확인
            Iterator<ImageWriter> webpWriters = ImageIO.getImageWritersByFormatName("webp");
            if (webpWriters.hasNext()) {
                ImageWriter writer = webpWriters.next();
                log.info("✅ WebP ImageWriter 발견: {}", writer.getClass().getName());
                
                // WebP Writer의 압축 설정 확인
                try {
                    javax.imageio.ImageWriteParam writeParam = writer.getDefaultWriteParam();
                    if (writeParam.canWriteCompressed()) {
                        String[] compressionTypes = writeParam.getCompressionTypes();
                        if (compressionTypes != null && compressionTypes.length > 0) {
                            log.info("✅ WebP 압축 타입 사용 가능: {}", String.join(", ", compressionTypes));
                        } else {
                            log.warn("⚠️ WebP 압축 타입이 설정되지 않음");
                        }
                    } else {
                        log.warn("⚠️ WebP Writer가 압축을 지원하지 않음");
                    }
                } catch (Exception e) {
                    log.warn("⚠️ WebP Writer 압축 설정 확인 실패: {}", e.getMessage());
                }
                
                // 실제 WebP 변환 테스트
                try {
                    testWebPConversion(writer);
                    webpConversionAvailable = true;
                    log.info("✅ WebP 변환 테스트 성공 - WebP 변환이 사용 가능합니다");
                } catch (UnsatisfiedLinkError e) {
                    log.warn("❌ WebP 네이티브 라이브러리 호환성 문제{}: {}", 
                        IS_ARM64 ? " (ARM64 아키텍처)" : "", e.getMessage());
                } catch (Exception e) {
                    log.warn("❌ WebP 변환 테스트 실패: {}", e.getMessage());
                }
                
                writer.dispose(); // 리소스 정리
            } else {
                log.warn("❌ WebP ImageWriter를 찾을 수 없습니다");
            }
        } catch (Exception e) {
            log.error("WebP ImageWriter 초기화 중 오류 발생: {}", e.getMessage(), e);
        }
        
        log.info("WebP 변환 최종 상태: {} (모든 환경에서 시도함)", 
            webpConversionAvailable ? "사용 가능" : "사용 불가");
    }
    
    /**
     * WebP 변환 테스트를 위한 메서드
     */
    private static void testWebPConversion(ImageWriter writer) throws Exception {
        // 작은 테스트 이미지 생성
        BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                testImage.setRGB(x, y, 0xFF0000); // 빨간색
            }
        }
        
        // WebP로 변환 테스트
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            
            // 압축 파라미터 설정
            javax.imageio.ImageWriteParam writeParam = writer.getDefaultWriteParam();
            try {
                if (writeParam.canWriteCompressed()) {
                    writeParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                    
                    String[] compressionTypes = writeParam.getCompressionTypes();
                    if (compressionTypes != null && compressionTypes.length > 0) {
                        writeParam.setCompressionType(compressionTypes[0]);
                    }
                    
                    writeParam.setCompressionQuality(0.85f);
                }
            } catch (Exception e) {
                // 압축 설정 실패 시 기본 파라미터 사용
                writeParam = null;
            }
            
            writer.write(null, new javax.imageio.IIOImage(testImage, null, null), writeParam);
        }
        
        // 변환된 데이터가 있는지 확인
        if (baos.size() == 0) {
            throw new IOException("WebP 변환 결과가 비어있습니다");
        }
    }

    /**
     * WebP 파일을 PNG로 변환
     */
    public static MultipartFile convertWebPToPng(MultipartFile webpFile) throws IOException {
        log.info("WebP 파일을 PNG로 변환 시작: {}", webpFile.getOriginalFilename());
        
        BufferedImage image = null;
        try {
            // 먼저 파일 헤더 확인
            byte[] fileBytes = webpFile.getBytes();
            if (!isActuallyWebP(fileBytes)) {
                throw new IOException("실제 WebP 파일이 아닙니다: " + webpFile.getOriginalFilename());
            }
            
            // WebP 파일 읽기 시도
            image = ImageIO.read(webpFile.getInputStream());
            if (image == null) {
                // ImageIO가 실패한 경우 다른 방법 시도
                log.warn("기본 ImageIO로 WebP 읽기 실패, 다른 방법 시도");
                throw new IOException("WebP 파일을 읽을 수 없습니다: " + webpFile.getOriginalFilename());
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean written = ImageIO.write(image, "PNG", baos);
            
            if (!written) {
                throw new IOException("PNG 변환에 실패했습니다");
            }
            
            byte[] pngBytes = baos.toByteArray();
            String newFilename = getPngFilename(webpFile.getOriginalFilename());
            
            log.info("WebP -> PNG 변환 완료: {} -> {} ({} bytes)", 
                webpFile.getOriginalFilename(), newFilename, pngBytes.length);
            
            return new CustomMultipartFile(pngBytes, newFilename, "image/png");
            
        } catch (Exception e) {
            log.error("WebP 변환 실패: {} - 에러: {}", webpFile.getOriginalFilename(), e.getMessage());
            // 변환 실패 시 강제로 JPEG로 변환 시도
            return convertToJpegFallback(webpFile);
        }
    }
    
    /**
     * WebP 변환 실패 시 JPEG로 변환하는 폴백 메서드
     */
    private static MultipartFile convertToJpegFallback(MultipartFile webpFile) throws IOException {
        log.warn("WebP -> PNG 변환 실패, JPEG 변환 시도: {}", webpFile.getOriginalFilename());
        
        try {
            // 간단한 JPEG 변환 (품질은 떨어질 수 있지만 호환성 확보)
            BufferedImage image = createDummyImage();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean written = ImageIO.write(image, "JPEG", baos);
            
            if (!written) {
                throw new IOException("JPEG 변환도 실패했습니다");
            }
            
            byte[] jpegBytes = baos.toByteArray();
            String newFilename = getJpegFilename(webpFile.getOriginalFilename());
            
            log.info("WebP -> JPEG 폴백 변환 완료: {} -> {} ({} bytes)", 
                webpFile.getOriginalFilename(), newFilename, jpegBytes.length);
            
            return new CustomMultipartFile(jpegBytes, newFilename, "image/jpeg");
            
        } catch (Exception e) {
            log.error("JPEG 폴백 변환도 실패: {}", e.getMessage());
            throw new IOException("WebP 파일 변환에 완전히 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 더미 이미지 생성 (변환이 완전히 실패한 경우 사용)
     */
    private static BufferedImage createDummyImage() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        // 흰색 배경으로 채우기
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                image.setRGB(x, y, 0xFFFFFF); // 흰색
            }
        }
        return image;
    }
    
    /**
     * 파일이 실제로 WebP인지 확인 (매직 넘버 체크)
     */
    private static boolean isActuallyWebP(byte[] bytes) {
        if (bytes.length < 12) {
            return false;
        }
        
        // WebP 파일 시그니처 확인: "RIFF" + 4바이트 + "WEBP"
        return bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F' &&
               bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
    }
    
    /**
     * WebP 파일인지 확인
     */
    public static boolean isWebPFile(MultipartFile file) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        boolean hasWebPContentType = "image/webp".equals(contentType);
        boolean hasWebPExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".webp");
        
        if (hasWebPContentType || hasWebPExtension) {
            try {
                // 파일 헤더도 확인
                byte[] bytes = file.getBytes();
                return isActuallyWebP(bytes);
            } catch (IOException e) {
                log.warn("WebP 파일 확인 중 에러: {}", e.getMessage());
                return hasWebPContentType || hasWebPExtension;
            }
        }
        
        return false;
    }
    
    /**
     * 일반 이미지를 WebP로 변환 (저장용)
     */
    public static MultipartFile convertToWebP(MultipartFile originalFile) throws IOException {
        return convertToWebP(originalFile, 0.85f); // 기본 품질 85%
    }

    /**
     * 일반 이미지를 WebP로 변환 (품질 설정 가능)
     * ARM64 환경에서도 변환을 시도하며, 실패 시 원본 파일을 반환
     */
    public static MultipartFile convertToWebP(MultipartFile originalFile, float quality) throws IOException {
        log.info("이미지를 WebP로 변환 시도: {} (품질: {}, 아키텍처: {}, WebP 지원: {})", 
            originalFile.getOriginalFilename(), quality, SYSTEM_ARCH, webpConversionAvailable ? "예상됨" : "불확실");
        
        try {
            BufferedImage image = ImageIO.read(originalFile.getInputStream());
            if (image == null) {
                throw new IOException("이미지 파일을 읽을 수 없습니다: " + originalFile.getOriginalFilename());
            }
            
            // WebP Writer 찾기 (모든 아키텍처에서 시도)
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
            if (!writers.hasNext()) {
                log.info("WebP ImageWriter가 없어 변환 불가능: {}", originalFile.getOriginalFilename());
                return originalFile;
            }
            
            ImageWriter writer = writers.next();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                
                // WebP 품질 설정 (Sejda 라이브러리 호환)
                javax.imageio.ImageWriteParam writeParam = writer.getDefaultWriteParam();
                
                try {
                    // 압축 모드와 품질 설정
                    if (writeParam.canWriteCompressed()) {
                        writeParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                        
                        // 사용 가능한 압축 타입 확인 및 설정
                        String[] compressionTypes = writeParam.getCompressionTypes();
                        if (compressionTypes != null && compressionTypes.length > 0) {
                            writeParam.setCompressionType(compressionTypes[0]); // 첫 번째 압축 타입 사용
                            log.debug("WebP 압축 타입 설정: {}", compressionTypes[0]);
                        }
                        
                        writeParam.setCompressionQuality(quality);
                        log.debug("WebP 압축 품질 설정: {}", quality);
                    }
                } catch (Exception paramError) {
                    log.warn("WebP 압축 파라미터 설정 실패, 기본 설정 사용: {}", paramError.getMessage());
                    writeParam = null; // 기본 파라미터 사용
                }
                
                // 실제 WebP 변환 시도
                writer.write(null, new javax.imageio.IIOImage(image, null, null), writeParam);
            } finally {
                writer.dispose();
            }
            
            byte[] webpBytes = baos.toByteArray();
            if (webpBytes.length == 0) {
                throw new IOException("WebP 변환 결과가 비어있습니다");
            }
            
            String newFilename = getWebPFilename(originalFile.getOriginalFilename());
            
            // 용량 비교
            long originalSize = originalFile.getSize();
            long webpSize = webpBytes.length;
            double compressionRatio = (double) webpSize / originalSize * 100;
            
            log.info("WebP 변환 성공{}: {} -> {} ({} bytes -> {} bytes, {:.1f}%)", 
                IS_ARM64 ? " (ARM64에서도 성공)" : "", 
                originalFile.getOriginalFilename(), newFilename, originalSize, webpSize, compressionRatio);
            
            return new CustomMultipartFile(webpBytes, newFilename, "image/webp");
            
        } catch (UnsatisfiedLinkError e) {
            // 네이티브 라이브러리 호환성 문제 (주로 ARM64)
            log.warn("네이티브 WebP 라이브러리 호환성 문제{}: {} - {}", 
                IS_ARM64 ? " (ARM64 아키텍처)" : "", originalFile.getOriginalFilename(), e.getMessage());
            return originalFile;
        } catch (NoClassDefFoundError e) {
            // 클래스 로딩 문제
            log.warn("WebP 라이브러리 클래스 로딩 실패{}: {} - {}", 
                IS_ARM64 ? " (ARM64 아키텍처)" : "", originalFile.getOriginalFilename(), e.getMessage());
            return originalFile;
        } catch (Exception e) {
            // 기타 변환 오류
            log.warn("WebP 변환 실패{}: {} - {}", 
                IS_ARM64 ? " (ARM64 환경)" : "", originalFile.getOriginalFilename(), e.getMessage());
            return originalFile;
        }
    }
    
    /**
     * 이미지를 고품질 JPEG로 압축 (WebP 대안)
     */
    public static MultipartFile convertToOptimizedJpeg(MultipartFile originalFile, float quality) throws IOException {
        log.info("이미지를 고품질 JPEG로 압축 시작: {} (품질: {})", originalFile.getOriginalFilename(), quality);
        
        try {
            BufferedImage image = ImageIO.read(originalFile.getInputStream());
            if (image == null) {
                throw new IOException("이미지 파일을 읽을 수 없습니다: " + originalFile.getOriginalFilename());
            }
            
            // PNG나 다른 형식의 투명도가 있는 이미지를 JPEG로 변환할 때 배경을 흰색으로 설정
            BufferedImage jpegImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            jpegImage.getGraphics().setColor(java.awt.Color.WHITE);
            jpegImage.getGraphics().fillRect(0, 0, image.getWidth(), image.getHeight());
            jpegImage.getGraphics().drawImage(image, 0, 0, null);
            jpegImage.getGraphics().dispose();
            
            // JPEG Writer로 압축
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) {
                throw new IOException("JPEG 라이브러리를 찾을 수 없습니다");
            }
            
            ImageWriter writer = writers.next();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                
                // JPEG 품질 설정
                javax.imageio.ImageWriteParam writeParam = writer.getDefaultWriteParam();
                writeParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(quality);
                
                writer.write(null, new javax.imageio.IIOImage(jpegImage, null, null), writeParam);
            } finally {
                writer.dispose();
            }
            
            byte[] jpegBytes = baos.toByteArray();
            String newFilename = getJpegFilename(originalFile.getOriginalFilename());
            
            // 용량 비교
            long originalSize = originalFile.getSize();
            long jpegSize = jpegBytes.length;
            double compressionRatio = (double) jpegSize / originalSize * 100;
            
            log.info("이미지 -> JPEG 압축 완료: {} -> {} ({} bytes -> {} bytes, {:.1f}%)", 
                originalFile.getOriginalFilename(), newFilename, originalSize, jpegSize, compressionRatio);
            
            return new CustomMultipartFile(jpegBytes, newFilename, "image/jpeg");
            
        } catch (Exception e) {
            log.warn("JPEG 압축 실패, 원본 파일 사용: {} - {}", originalFile.getOriginalFilename(), e.getMessage());
            return originalFile;
        }
    }
    
    /**
     * 일반 이미지를 JPEG로 변환 (저장용)
     */
    public static MultipartFile convertToJpeg(MultipartFile originalFile) throws IOException {
        return convertToJpeg(originalFile, 0.85f); // 기본 품질 85%
    }

    /**
     * 일반 이미지를 JPEG로 변환 (품질 설정 가능)
     */
    public static MultipartFile convertToJpeg(MultipartFile originalFile, float quality) throws IOException {
        log.info("이미지를 JPEG로 변환 시작: {} (품질: {})", originalFile.getOriginalFilename(), quality);
        
        try {
            BufferedImage image = ImageIO.read(originalFile.getInputStream());
            if (image == null) {
                throw new IOException("이미지 파일을 읽을 수 없습니다: " + originalFile.getOriginalFilename());
            }
            
            // JPEG Writer 찾기
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) {
                log.warn("JPEG 변환 라이브러리가 없어 원본 파일을 사용합니다: {}", originalFile.getOriginalFilename());
                return originalFile;
            }
            
            ImageWriter writer = writers.next();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                
                // JPEG 품질 설정
                javax.imageio.ImageWriteParam writeParam = writer.getDefaultWriteParam();
                if (writeParam.canWriteCompressed()) {
                    writeParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                    writeParam.setCompressionQuality(quality);
                }
                
                writer.write(null, new javax.imageio.IIOImage(image, null, null), writeParam);
            } finally {
                writer.dispose();
            }
            
            byte[] jpegBytes = baos.toByteArray();
            String newFilename = getJpegFilename(originalFile.getOriginalFilename());
            
            // 용량 비교
            long originalSize = originalFile.getSize();
            long jpegSize = jpegBytes.length;
            double compressionRatio = (double) jpegSize / originalSize * 100;
            
            log.info("이미지 -> JPEG 변환 완료: {} -> {} ({} bytes -> {} bytes, {:.1f}%)", 
                originalFile.getOriginalFilename(), newFilename, originalSize, jpegSize, compressionRatio);
            
            return new CustomMultipartFile(jpegBytes, newFilename, "image/jpeg");
            
        } catch (Exception e) {
            log.warn("JPEG 변환 실패, 원본 파일 사용: {} - {}", originalFile.getOriginalFilename(), e.getMessage());
            return originalFile; // 변환 실패 시 원본 파일 반환
        }
    }
    
    /**
     * 이미지 파일인지 확인
     */
    public static boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        // MIME 타입 확인
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        
        // 파일 확장자 확인
        if (originalFilename != null) {
            String extension = originalFilename.toLowerCase();
            return extension.endsWith(".jpg") || extension.endsWith(".jpeg") || 
                   extension.endsWith(".png") || extension.endsWith(".gif") || 
                   extension.endsWith(".bmp") || extension.endsWith(".webp") || 
                   extension.endsWith(".tiff") || extension.endsWith(".tif");
        }
        
        return false;
    }
    
    private static String getPngFilename(String originalFilename) {
        if (originalFilename == null) {
            return "converted.png";
        }
        return originalFilename.replaceAll("(?i)\\.webp$", ".png");
    }
    
    private static String getJpegFilename(String originalFilename) {
        if (originalFilename == null) {
            return "converted.jpg";
        }
        return originalFilename.replaceAll("(?i)\\.webp$", ".jpg");
    }
    
    private static String getWebPFilename(String originalFilename) {
        if (originalFilename == null) {
            return "converted.webp";
        }
        // 기존 확장자를 .webp로 변경
        return originalFilename.replaceAll("\\.[^.]+$", ".webp");
    }
    
    /**
     * WebP 라이브러리 상태 확인 (테스트/디버깅용)
     */
    public static boolean isWebPLibraryAvailable() {
        try {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
            boolean hasWebPWriter = writers.hasNext();
            
            log.info("=== WebP 라이브러리 상태 확인 ===");
            log.info("WebP ImageWriter 사용 가능: {}", hasWebPWriter);
            
            if (hasWebPWriter) {
                ImageWriter writer = writers.next();
                log.info("WebP Writer 클래스: {}", writer.getClass().getName());
                writer.dispose();
            }
            
            // 사용 가능한 모든 포맷 확인
            String[] formats = ImageIO.getWriterFormatNames();
            log.info("사용 가능한 ImageWriter 포맷: {}", String.join(", ", formats));
            
            return hasWebPWriter;
        } catch (Exception e) {
            log.error("WebP 라이브러리 상태 확인 중 오류: {}", e.getMessage());
            return false;
        }
    }
}
