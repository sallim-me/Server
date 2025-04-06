package me.sallim.api.domain.member.dto.request;

import lombok.Builder;

@Builder
public record MemberJoinRequestDTO(
        String username,   // 아이디 (로그인용)
        String password,   // 비밀번호
        String nickname,   // 닉네임
        String name,       // 실명
        Boolean isBuyer    // 바이어 여부
) {}