package me.sallim.api.global.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponseDTO(
        @JsonProperty("access-token") String accessToken,
        @JsonProperty("refresh-token") String refreshToken
) {}
