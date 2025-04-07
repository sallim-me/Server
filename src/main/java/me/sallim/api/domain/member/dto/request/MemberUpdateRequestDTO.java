package me.sallim.api.domain.member.dto.request;

public record MemberUpdateRequestDTO(
        String username,
        String name,
        String password,
        String nickname,
        Boolean isBuyer
) {}