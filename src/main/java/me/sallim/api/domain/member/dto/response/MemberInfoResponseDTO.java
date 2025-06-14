package me.sallim.api.domain.member.dto.response;

public record MemberInfoResponseDTO(
    Long memberId,
    String username,
    String name,
    String nickname,
    Boolean isBuyer
) {}
