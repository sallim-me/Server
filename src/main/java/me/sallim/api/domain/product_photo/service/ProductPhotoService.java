package me.sallim.api.domain.product_photo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public ProductPhotoResponse uploadPhoto(Long productId, MultipartFile file) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = "product/" + productId + "/" + UUID.randomUUID() + "_" + timestamp + extension;

            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Upload to MinIO with PUBLIC_READ access
            try {
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(fileName)
                                .contentType(contentType)
                                .acl("public-read") // 공개 읽기 권한 설정
                                .build(),
                        RequestBody.fromBytes(file.getBytes()));

                log.info("File uploaded with public-read ACL");
            } catch (Exception e) {
                log.warn("Failed to set public-read ACL, uploading without ACL: {}", e.getMessage());
                // ACL이 실패하면 ACL 없이 시도
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(fileName)
                                .contentType(contentType)
                                .build(),
                        RequestBody.fromBytes(file.getBytes()));
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
                    .fileSize(file.getSize())
                    .build();

            productPhotoRepository.save(productPhoto);

            return convertToResponse(productPhoto);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
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

    @Transactional(readOnly = true)
    public ProductPhotoResponse getPhotoById(Long productId, Long photoId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        ProductPhoto photo = productPhotoRepository.findByProductAndId(product, photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found with id: " + photoId));

        return convertToResponse(photo);
    }

    @Transactional
    public void deletePhoto(Long productId, Long photoId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        ProductPhoto photo = productPhotoRepository.findByProductAndId(product, photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found with id: " + photoId));

        // Delete from MinIO
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(photo.getFileName())
                .build());

        // Delete from database
        productPhotoRepository.delete(photo);
    }

    @Transactional
    public ProductPhotoResponse updatePhoto(Long productId, Long photoId, MultipartFile file) {
        try {
            // First delete the old photo
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

            ProductPhoto photo = productPhotoRepository.findByProductAndId(product, photoId)
                    .orElseThrow(() -> new IllegalArgumentException("Photo not found with id: " + photoId));

            // Delete old file from MinIO
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(photo.getFileName())
                    .build());

            // Upload new file
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = "product/" + productId + "/" + UUID.randomUUID() + "_" + timestamp + extension;

            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Upload to MinIO with PUBLIC_READ access
            try {
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(fileName)
                                .contentType(contentType)
                                .acl("public-read") // 공개 읽기 권한 설정
                                .build(),
                        RequestBody.fromBytes(file.getBytes()));

                log.info("File updated with public-read ACL");
            } catch (Exception e) {
                log.warn("Failed to set public-read ACL, uploading without ACL: {}", e.getMessage());
                // ACL이 실패하면 ACL 없이 시도
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(fileName)
                                .contentType(contentType)
                                .build(),
                        RequestBody.fromBytes(file.getBytes()));
            }

            // DB에는 파일 경로만 저장 (bucket/fileName)
            String fileKey = bucket + "/" + fileName;

            log.info("Storing file key in DB: {}", fileKey);
            log.info("Full URL would be: {}/{}", endpoint, fileKey);

            // Update photo info in database
            photo.updatePhoto(fileName, fileKey, contentType, file.getSize());

            return convertToResponse(photo);
        } catch (IOException e) {
            log.error("Failed to update file", e);
            throw new RuntimeException("Failed to update file: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private ProductPhotoResponse convertToResponse(ProductPhoto photo) {
        // 응답할 때 endpoint를 붙여서 완전한 URL 생성
        String fullUrl = endpoint + "/" + photo.getFileUrl();

        return ProductPhotoResponse.builder()
                .id(photo.getId())
                .productId(photo.getProduct().getId())
                .fileName(photo.getFileName())
                .fileUrl(fullUrl) // 완전한 URL 반환
                .contentType(photo.getContentType())
                .fileSize(photo.getFileSize())
                .createdAt(photo.getCreatedAt())
                .updatedAt(photo.getUpdatedAt())
                .build();
    }
}
