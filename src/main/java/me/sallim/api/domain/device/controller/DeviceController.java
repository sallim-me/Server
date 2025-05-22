package me.sallim.api.domain.device.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.device.dto.DeviceInfoResponse;
import me.sallim.api.domain.device.service.DeviceService;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "모델명으로 기기 정보 조회", description = """
        모델명을 입력하면 해당 기기의 브랜드, 모델명, 옵션, 가격 등의 정보를 조회합니다.
        
        ### 요청 예시 (Query Parameter):
        ```
        /device/info?modelName=RT58K7100BS
        ```

        ### 응답 예시:
        ```json
        {
          "status": 200,
          "code": "SUCCESS",
          "message": "요청이 성공했습니다.",
          "data": {
            "brand": "삼성",
            "category": "냉장고",
            "modelName": "RT58K7100BS",
            "deviceName": "삼성 냉장고 580L",
            "price": 850000,
            "option": "실버/양문형",
            "date": "2024-05-01"
          }
        }
        ```
    """)
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<DeviceInfoResponse>> getDeviceInfo(@RequestParam String modelName) {
        DeviceInfoResponse info = deviceService.getDeviceInfo(modelName);
        return ResponseEntity.ok(ApiResponse.success(info));
    }
}