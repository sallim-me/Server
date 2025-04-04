package me.sallim.api.global.security.dto;

public record LoginRequestDTO(
        String username,
        String password
) {}
