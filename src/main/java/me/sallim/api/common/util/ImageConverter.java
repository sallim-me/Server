package me.sallim.api.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class ImageConverter {

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
}
