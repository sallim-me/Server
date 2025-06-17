package me.sallim.api.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.converter.MemberConverter;
import me.sallim.api.domain.member.dto.request.MemberJoinRequestDTO;
import me.sallim.api.domain.member.dto.request.MemberUpdateRequestDTO;
import me.sallim.api.domain.member.dto.request.UpdateFcmTokenRequest;
import me.sallim.api.domain.member.dto.response.MemberInfoResponseDTO;
import me.sallim.api.domain.member.dto.response.MemberPostSummaryResponse;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.domain.member.service.MemberService;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Operation(
            summary = "기존 회원 username 중복 체크",
            description = """
                회원가입 시 사용하려는 username이 이미 존재하는지 확인합니다.
                ### Path Variable:
                - `username` : 중복 체크할 username

                ### 응답 예시:
                ```json
                {
                  "status": 200,
                  "code": "SUCCESS",
                  "message": "사용 가능한 username입니다."
                }
                ```
                """
    )
    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse<String>> checkUsername(@PathVariable String username) {
        boolean isAvailable = memberService.isUsernameAvailable(username);
        if (isAvailable) {
            return ResponseEntity.ok(ApiResponse.success("사용 가능한 username입니다."));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("이미 사용 중인 username입니다.", "USERNAME_ALREADY_EXISTS"));
        }
    }

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

    @Operation(
            summary = "내 정보 조회",
            description = """
        로그인한 사용자의 프로필 정보를 반환합니다.  
        서버는 헤더의 로그인 토큰을 통해 사용자를 식별합니다.

        ### 응답 예시:
        ```json
        {
          "status": 200,
          "code": "SUCCESS",
          "message": "요청이 성공했습니다.",
          "data": {
            "username": "minjung123",
            "name": "김민정",
            "nickname": "민짱",
            "isBuyer": true
          }
        }
        ```
        """
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponseDTO>> getMyInfo(@LoginMember Member member) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMyInfo(member)));
    }

    @Operation(
            summary = "내 글 전체 조회",
            description = """
        로그인한 사용자가 작성한 모든 글(판매글, 구매글)을 리스트로 반환합니다.
        각 글에는 제목, 글 종류(BUYING/SELLING), 작성일, 활성 상태 등이 포함됩니다.

        ### 응답 예시:
        ```json
        {
          "status": 200,
          "code": "SUCCESS",
          "message": "요청이 성공했습니다.",
          "data": [
            {
              "productId": 12,
              "title": "삼성 냉장고 팝니다",
              "postType": "SELLING",
              "isActive": true,
              "createdAt": "2024-05-01T14:30:00"
            },
            {
              "productId": 7,
              "title": "세탁기 10대 구매 희망",
              "postType": "BUYING",
              "isActive": false,
              "createdAt": "2024-04-28T11:10:00"
            }
          ]
        }
        ```
        """
    )
    @GetMapping("/me/posts")
    public ResponseEntity<ApiResponse<List<MemberPostSummaryResponse>>> getMyPosts(@LoginMember Member member) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMyPosts(member)));
    }

    @PutMapping("/fcm-token")
    @Operation(summary = "FCM 토큰 업데이트")
    public ResponseEntity<ApiResponse<Void>> updateFcmToken(
            @LoginMember Member member,
            @RequestBody UpdateFcmTokenRequest request) {
        memberService.updateFcmToken(member.getId(), request.getFcmToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}