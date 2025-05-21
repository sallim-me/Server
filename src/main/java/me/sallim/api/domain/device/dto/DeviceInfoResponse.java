package me.sallim.api.domain.device.dto;

import lombok.Builder;

@Builder
public record DeviceInfoResponse(
        String brand,
        String category,
        String modelName,
        String deviceName,
        Integer price,
        String date
) {}
