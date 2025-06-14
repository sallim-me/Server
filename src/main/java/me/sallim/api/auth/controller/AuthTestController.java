package me.sallim.api.auth.controller;

import me.sallim.api.domain.member.model.Member;
import me.sallim.api.global.annotation.LoginMember;
import me.sallim.api.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 테스트를 위한 컨트롤러
 */
@RestController
@RequestMapping("/test")
public class AuthTestController {

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("공개 엔드포인트 - 인증 불필요"));
    }

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<String>> protectedEndpoint(@LoginMember Member member) {
        return ResponseEntity.ok(ApiResponse.success("보호된 엔드포인트 - 사용자: " + member.getUsername()));
    }
}
