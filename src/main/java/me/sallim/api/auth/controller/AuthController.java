package me.sallim.api.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.sallim.api.auth.service.AuthService;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import me.sallim.api.global.security.dto.LoginRequestDTO;
import me.sallim.api.global.security.dto.TokenResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "로그인 API",
            description = """
                아이디/비밀번호로 로그인합니다.
                ### 요청 예시 JSON:
                ```json
                {
                  "username": "testuser",
                  "password": "1234"
                }
                ```
                """
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        TokenResponseDTO token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "로그아웃 API",
            description = "로그인된 사용자의 RefreshToken을 삭제하여 로그아웃 처리합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@LoginMember Member member) {
        authService.logout(member.getId());
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
    }

    @Operation(
            summary = "Access/Refresh 토큰 재발급 API",
            description = """
    저장된 Refresh Token을 통해 Access Token을 재발급합니다.
    efresh Token을 헤더에 설정한 후 요청.

    ### 응답 예시:
    ```json
    {
      "access-token": "new-access-token-abc",
      "refresh-token": "new-refresh-token-xyz"
    }
    ```
    """
    )
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader("Authorization");
        TokenResponseDTO token = authService.reissue(refreshTokenHeader);
        return ResponseEntity.ok(ApiResponse.success(token));
    }
}