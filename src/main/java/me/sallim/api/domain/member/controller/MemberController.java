package me.sallim.api.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.converter.MemberConverter;
import me.sallim.api.domain.member.dto.request.MemberJoinRequestDTO;
import me.sallim.api.domain.member.dto.request.MemberUpdateRequestDTO;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.domain.member.service.MemberService;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;


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

    @Operation(
            summary = "회원 정보 조회",
            description = """
        특정 회원의 정보를 조회합니다.
        ### Path Variable:
        - `memberId` : 조회할 회원의 ID

        ### 응답 예시:
        ```json
        {
          "username": "testuser",
          "name": "김민경",
          "nickname": "민경짱",
          "isBuyer": false
        }
        ```
        """
    )
    @GetMapping("/{memberId}")
    public ResponseEntity<?> getMemberById(@PathVariable Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        return ResponseEntity.ok(ApiResponse.success(MemberConverter.toMemberInfo(member)));
    }

    @Operation(summary = "회원 정보 수정", description = """
    회원 아이디, 이름, 비밀번호, 닉네임, 바이어 여부를 수정합니다.

    ### 요청 예시:
    ```json
    {
      "username": "newuser",
      "name": "김민경",
      "password": "newpass123",
      "nickname": "민경이",
      "isBuyer": true
    }
    ```
    """)
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@LoginMember Member member,
                                           @RequestBody MemberUpdateRequestDTO request) {
        memberService.updateProfile(member, request);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 완료"));
    }
}