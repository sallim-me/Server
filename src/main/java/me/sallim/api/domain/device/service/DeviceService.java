package me.sallim.api.domain.device.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.device.dto.DeviceInfoResponse;
import me.sallim.api.domain.device.model.Device;
import me.sallim.api.domain.device.repository.DeviceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceInfoResponse getDeviceInfo(String modelName) {
        Device device = deviceRepository.findFirstByModelNameOrderByIdDesc(modelName)
                .orElseThrow(() -> new IllegalArgumentException("해당 모델명을 가진 기기를 찾을 수 없습니다."));

        return DeviceInfoResponse.builder()
                .brand(device.getBrand())
                .category(device.getCategory())
                .modelName(device.getModelName())
                .deviceName(device.getDeviceName())
                .price(device.getPrice())
                .date(device.getDate())
                .build();
    }
}