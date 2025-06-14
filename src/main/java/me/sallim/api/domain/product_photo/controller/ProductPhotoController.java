package me.sallim.api.domain.product_photo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.product_photo.dto.ProductPhotoResponse;
import me.sallim.api.domain.product_photo.service.ProductPhotoService;
import me.sallim.api.global.response.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/{productId}/photos")
@Tag(name = "Product Photo", description = "상품 사진 관련 API")
public class ProductPhotoController {

    private final ProductPhotoService productPhotoService;

    @Operation(
            summary = "상품 사진 업로드",
            description = """
            상품에 사진을 업로드합니다.
            
            ### 요청 형식:
            - 요청은 multipart/form-data 형식으로 보내야 합니다.
            - file: 업로드할 이미지 파일
            
            ### 응답 필드:
            - id: 사진 ID
            - productId: 상품 ID
            - fileName: 저장된 파일명
            - fileUrl: 파일 접근 URL
            - contentType: 파일 타입 (예: image/jpeg, image/png)
            - fileSize: 파일 크기 (바이트)
            - createdAt: 생성일시
            - updatedAt: 업데이트일시
            
            ### 응답 예시:
            ```json
            {
              "id": 1,
              "productId": 123,
              "fileName": "product/123/abc123_20240602123456.jpg",
              "fileUrl": "minio/bucket-name/product/123/abc123_20240602123456.jpg",
              "contentType": "image/jpeg",
              "fileSize": 1024000,
              "createdAt": "2024-06-02T12:34:56",
              "updatedAt": "2024-06-02T12:34:56"
            }
            ```
            """
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductPhotoResponse> uploadPhoto(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "업로드할 이미지 파일") @RequestPart("file") MultipartFile file) {
        ProductPhotoResponse response = productPhotoService.uploadPhotoAndGetResponse(productId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "상품 사진 목록 조회",
            description = """
            특정 상품에 등록된 모든 사진을 조회합니다.
            
            ### 응답 필드:
            - id: 사진 ID
            - productId: 상품 ID
            - fileName: 저장된 파일명
            - fileUrl: 파일 접근 URL
            - contentType: 파일 타입 (예: image/jpeg, image/png)
            - fileSize: 파일 크기 (바이트)
            - createdAt: 생성일시
            - updatedAt: 업데이트일시
            
            ### 응답 예시:
            ```json
            [
              {
                "id": 1,
                "productId": 123,
                "fileName": "product/123/abc123_20240602123456.jpg",
                "fileUrl": "minio/bucket-name/product/123/abc123_20240602123456.jpg",
                "contentType": "image/jpeg",
                "fileSize": 1024000,
                "createdAt": "2024-06-02T12:34:56",
                "updatedAt": "2024-06-02T12:34:56"
              },
              {
                "id": 2,
                "productId": 123,
                "fileName": "product/123/def456_20240602123456.jpg",
                "fileUrl": "minio/bucket-name/product/123/def456_20240602123456.jpg",
                "contentType": "image/jpeg",
                "fileSize": 2048000,
                "createdAt": "2024-06-02T12:35:00",
                "updatedAt": "2024-06-02T12:35:00"
              }
            ]
            ```
            """
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductPhotoResponse>>> getPhotos(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        List<ProductPhotoResponse> photos = productPhotoService.getPhotosByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(photos));
    }

    @Operation(
            summary = "상품 사진 상세 조회",
            description = """
            특정 상품의 특정 사진 하나를 조회합니다.
            
            ### 응답 필드:
            - id: 사진 ID
            - productId: 상품 ID
            - fileName: 저장된 파일명
            - fileUrl: 파일 접근 URL
            - contentType: 파일 타입 (예: image/jpeg, image/png)
            - fileSize: 파일 크기 (바이트)
            - createdAt: 생성일시
            - updatedAt: 업데이트일시
            
            ### 응답 예시:
            ```json
            {
              "id": 1,
              "productId": 123,
              "fileName": "product/123/abc123_20240602123456.jpg",
              "fileUrl": "minio/bucket-name/product/123/abc123_20240602123456.jpg",
              "contentType": "image/jpeg",
              "fileSize": 1024000,
              "createdAt": "2024-06-02T12:34:56",
              "updatedAt": "2024-06-02T12:34:56"
            }
            ```
            """
    )
    @GetMapping("/{photoId}")
    public ResponseEntity<ApiResponse<ProductPhotoResponse>> getPhoto(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "사진 ID") @PathVariable Long photoId) {
        ProductPhotoResponse photo = productPhotoService.getPhotoById(productId, photoId);
        return ResponseEntity.ok(ApiResponse.success(photo));
    }

    @Operation(
            summary = "상품 사진 수정",
            description = """
            특정 상품의 특정 사진을 새로운 사진으로 수정합니다.
            기존 사진은 삭제되고 새로운 사진이 업로드됩니다.
            
            ### 요청 형식:
            - 요청은 multipart/form-data 형식으로 보내야 합니다.
            - file: 업로드할 새 이미지 파일
            
            ### 응답 필드:
            - id: 사진 ID (기존 ID 유지)
            - productId: 상품 ID
            - fileName: 저장된 파일명 (새 파일명)
            - fileUrl: 파일 접근 URL (새 URL)
            - contentType: 파일 타입 (예: image/jpeg, image/png)
            - fileSize: 파일 크기 (바이트)
            - createdAt: 생성일시 (기존 시간 유지)
            - updatedAt: 업데이트일시 (현재 시간으로 변경)
            
            ### 응답 예시:
            ```json
            {
              "id": 1,
              "productId": 123,
              "fileName": "product/123/xyz789_20240602143000.jpg",
              "fileUrl": "minio/bucket-name/product/123/xyz789_20240602143000.jpg",
              "contentType": "image/jpeg",
              "fileSize": 1048576,
              "createdAt": "2024-06-02T12:34:56",
              "updatedAt": "2024-06-02T14:30:00"
            }
            ```
            """
    )
    @PutMapping(value = "/{photoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductPhotoResponse> updatePhoto(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "사진 ID") @PathVariable Long photoId,
            @Parameter(description = "업로드할 새 이미지 파일") @RequestPart("file") MultipartFile file) {
        ProductPhotoResponse response = productPhotoService.updatePhoto(productId, photoId, file);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "상품 사진 삭제",
            description = """
            특정 상품의 특정 사진을 삭제합니다.
            삭제된 사진은 복구할 수 없습니다.
            
            ### 응답:
            - 204 No Content: 삭제 성공
            - 404 Not Found: 해당 상품 또는 사진이 존재하지 않음
            """
    )
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "사진 ID") @PathVariable Long photoId) {
        productPhotoService.deletePhoto(productId, photoId);
        return ResponseEntity.noContent().build();
    }
}
