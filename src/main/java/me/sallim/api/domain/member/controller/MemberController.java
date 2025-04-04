package me.sallim.api.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.dto.MemberJoinRequestDTO;
import me.sallim.api.domain.member.service.MemberService;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원 프로필 설정 (회원가입)",
            description = """
                회원가입 시 프로필 정보를 등록합니다.
                ### 요청 예시 JSON:
                ```json
                {
                  "username": "testuser",
                  "password": "1234",
                  "nickname": "테스터",
                  "name": "김민경",
                  "isBuyer": false
                }
                ```
                """
    )
    @PostMapping("/profile")
    public ResponseEntity<?> join(@RequestBody MemberJoinRequestDTO request) {
        memberService.join(request);
        return ResponseEntity.ok(
                ApiResponse.success("프로필 설정 완료")
        );
    }
}