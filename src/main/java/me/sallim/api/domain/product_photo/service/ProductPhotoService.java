package me.sallim.api.domain.product_photo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sallim.api.common.util.ImageConverter;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.repository.ProductRepository;
import me.sallim.api.domain.product_photo.dto.ProductPhotoResponse;
import me.sallim.api.domain.product_photo.model.ProductPhoto;
import me.sallim.api.domain.product_photo.repository.ProductPhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPhotoService {

    private final S3Client s3Client;
    private final ProductPhotoRepository productPhotoRepository;
    private final ProductRepository productRepository;

    @Value("${spring.minio.bucket}")
    private String bucket;

    @Value("${spring.minio.endpoint}")
    private String endpoint;

    /**
     * 제품 사진을 업로드합니다.
     *
     * @param productId 제품 ID
     * @param file 업로드할 파일
     * @return 업로드된 사진 엔티티
     */
    @Transactional
    public ProductPhoto uploadPhoto(Long productId, MultipartFile file) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

            // 이미지를 WebP로 변환 (저장 용량 최적화)
            MultipartFile processedFile = convertToWebPForStorage(file);

            String originalFilename = processedFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = "product/" + productId + "/" + UUID.randomUUID() + "_" + timestamp + extension;

            String contentType = processedFile.getContentType();
            if (contentType == null) {
                // MIME 타입이 없는 경우 파일 확장자로 추정
                switch (extension.toLowerCase()) {
                    case ".jpg", ".jpeg" -> contentType = "image/jpeg";
                    case ".png" -> contentType = "image/png";
                    case ".gif" -> contentType = "image/gif";
                    case ".bmp" -> contentType = "image/bmp";
                    case ".webp" -> contentType = "image/webp";
                    case ".tiff", ".tif" -> contentType = "image/tiff";
                    default -> contentType = "application/octet-stream";
                }
                log.info("MIME 타입이 누락되어 파일 확장자로 추정: {} -> {}", extension, contentType);
            }

            // Upload to MinIO with PUBLIC_READ access
            try {
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(fileName)
                                .contentType(contentType)
                                .acl("public-read") // 공개 읽기 권한 설정
                                .build(),
                        RequestBody.fromBytes(processedFile.getBytes()));

                log.info("File uploaded with public-read ACL");
            } catch (Exception e) {
                log.warn("Failed to set public-read ACL, uploading without ACL: {}", e.getMessage());
                // ACL이 실패하면 ACL 없이 시도
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(fileName)
                                .contentType(contentType)
                                .build(),
                        RequestBody.fromBytes(processedFile.getBytes()));
            }

            // DB에는 파일 경로만 저장 (bucket/fileName)
            String fileKey = bucket + "/" + fileName;

            log.info("Storing file key in DB: {}", fileKey);
            log.info("Full URL would be: {}/{}", endpoint, fileKey);

            // Save photo info to database
            ProductPhoto productPhoto = ProductPhoto.builder()
                    .product(product)
                    .fileName(fileName)
                    .fileUrl(fileKey) // endpoint 없이 저장
                    .contentType(contentType)
                    .fileSize(processedFile.getSize())
                    .build();

            return productPhotoRepository.save(productPhoto);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * 제품 사진을 업로드하고 응답 DTO를 반환합니다.
     *
     * @param productId 제품 ID
     * @param file 업로드할 파일
     * @return 업로드된 사진 응답 DTO
     */
    @Transactional
    public ProductPhotoResponse uploadPhotoAndGetResponse(Long productId, MultipartFile file) {
        ProductPhoto photo = uploadPhoto(productId, file);
        return convertToResponse(photo);
    }

    @Transactional(readOnly = true)
    public List<ProductPhotoResponse> getPhotosByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        List<ProductPhoto> photos = productPhotoRepository.findByProduct(product);
        return photos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 제품의 특정 사진을 조회합니다.
     *
     * @param productId 제품 ID
     * @param photoId 사진 ID
     * @return 사진 응답 DTO
     */
    @Transactional(readOnly = true)
    public ProductPhotoResponse getPhotoById(Long productId, Long photoId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        ProductPhoto photo = productPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found with id: " + photoId));

        // 제품과 사진의 연관관계 확인
        if (!photo.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("Photo " + photoId + " does not belong to Product " + productId);
        }

        return convertToResponse(photo);
    }

    /**
     * 특정 제품의 특정 사진을 새로운 사진으로 업데이트합니다.
     *
     * @param productId 제품 ID
     * @param photoId 사진 ID
     * @param file 새로 업로드할 파일
     * @return 업데이트된 사진 응답 DTO
     */
    @Transactional
    public ProductPhotoResponse updatePhoto(Long productId, Long photoId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        ProductPhoto existingPhoto = productPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found with id: " + photoId));

        // 제품과 사진의 연관관계 확인
        if (!existingPhoto.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("Photo " + photoId + " does not belong to Product " + productId);
        }

        try {
            // S3에서 기존 파일 삭제
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(existingPhoto.getFileName())
                        .build());
                log.info("Deleted existing file from S3: {}", existingPhoto.getFileName());
            } catch (Exception e) {
                log.warn("Failed to delete existing file from S3: {}", e.getMessage());
            }

            // 새 파일 업로드 준비
            MultipartFile processedFile = convertToWebPForStorage(file);
            String originalFilename = processedFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = "product/" + productId + "/" + UUID.randomUUID() + "_" + timestamp + extension;

            String contentType = processedFile.getContentType();
            if (contentType == null) {
                // MIME 타입이 없는 경우 파일 확장자로 추정
                switch (extension.toLowerCase()) {
                    case ".jpg", ".jpeg" -> contentType = "image/jpeg";
                    case ".png" -> contentType = "image/png";
                    case ".gif" -> contentType = "image/gif";
                    case ".bmp" -> contentType = "image/bmp";
                    case ".webp" -> contentType = "image/webp";
                    case ".tiff", ".tif" -> contentType = "image/tiff";
                    default -> contentType = "application/octet-stream";
                }
                log.info("MIME 타입이 누락되어 파일 확장자로 추정: {} -> {}", extension, contentType);
            }

            // S3에 새 파일 업로드
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(processedFile.getBytes()));

            // DB에는 파일 경로만 저장 (bucket/fileName)
            String fileKey = bucket + "/" + fileName;

            // 기존 photo 엔티티 업데이트
            existingPhoto.updatePhoto(
                    fileName,
                    fileKey,
                    contentType,
                    processedFile.getSize()
            );

            ProductPhoto updatedPhoto = productPhotoRepository.save(existingPhoto);
            return convertToResponse(updatedPhoto);
        } catch (IOException e) {
            log.error("Failed to update file", e);
            throw new RuntimeException("Failed to update file: " + e.getMessage());
        }
    }

    /**
     * 특정 제품의 특정 사진을 삭제합니다.
     *
     * @param productId 제품 ID
     * @param photoId 사진 ID
     */
    @Transactional
    public void deletePhoto(Long productId, Long photoId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        ProductPhoto photo = productPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found with id: " + photoId));

        // 제품과 사진의 연관관계 확인
        if (!photo.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("Photo " + photoId + " does not belong to Product " + productId);
        }

        // 썸네일로 설정된 사진인지 확인
        if (product.getProductPhotoId() != null && product.getProductPhotoId().getId().equals(photoId)) {
            // 썸네일로 설정된 사진인 경우, 썸네일 설정 제거
            product.setProductPhotoId(null);
            productRepository.save(product);
        }

        // S3에서 파일 삭제
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(photo.getFileName())
                    .build());
            log.info("Deleted file from S3: {}", photo.getFileName());
        } catch (Exception e) {
            log.warn("Failed to delete file from S3: {}", e.getMessage());
        }

        // DB에서 정보 삭제
        productPhotoRepository.delete(photo);
    }

    /**
     * 저장용 이미지를 최적화 (WebP 우선, 실패 시 고품질 JPEG)
     */
    private MultipartFile convertToWebPForStorage(MultipartFile file) {
        try {
            // 이미 WebP 파일인 경우 그대로 사용
            if (ImageConverter.isWebPFile(file)) {
                log.info("이미 WebP 파일이므로 변환하지 않음: {}", file.getOriginalFilename());
                return file;
            }
            
            // 이미지 파일인지 확인
            if (!ImageConverter.isImageFile(file)) {
                log.warn("이미지 파일이 아니므로 최적화하지 않음: {}", file.getOriginalFilename());
                return file;
            }
            
            // WebP 변환 시도 (변환 가능한 환경에서만)
            try {
                MultipartFile webpResult = ImageConverter.convertToWebP(file, 0.85f);
                
                // WebP 변환이 성공했는지 확인
                if (webpResult != file && webpResult.getOriginalFilename() != null && 
                    webpResult.getOriginalFilename().toLowerCase().endsWith(".webp")) {
                    log.info("WebP 변환 성공: {} -> {}", file.getOriginalFilename(), webpResult.getOriginalFilename());
                    return webpResult;
                }
                
                // WebP 변환이 원본을 반환한 경우 (변환 불가능한 환경)
                log.info("WebP 변환 불가능, 원본 파일 사용: {}", file.getOriginalFilename());
                return file;
                
            } catch (Exception webpError) {
                log.info("WebP 변환 실패 ({}), 원본 파일 사용: {}", 
                    webpError.getClass().getSimpleName(), file.getOriginalFilename());
                return file;
            }
            
        } catch (Exception e) {
            log.warn("이미지 최적화 실패, 원본 파일 사용: {} - {}", file.getOriginalFilename(), e.getMessage());
            return file;
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private ProductPhotoResponse convertToResponse(ProductPhoto photo) {
        return ProductPhotoResponse.builder()
                .id(photo.getId())
                .productId(photo.getProduct().getId())
                .fileName(photo.getFileName())
                .fileUrl(endpoint + "/" + photo.getFileUrl())
                .contentType(photo.getContentType())
                .fileSize(photo.getFileSize())
                .build();
    }
}
