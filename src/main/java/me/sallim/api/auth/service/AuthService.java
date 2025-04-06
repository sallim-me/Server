package me.sallim.api.auth.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.global.security.dto.LoginRequestDTO;
import me.sallim.api.global.security.dto.TokenResponseDTO;
import me.sallim.api.global.security.jwt.JwtTokenProvider;
import me.sallim.api.global.security.jwt.RefreshToken;
import me.sallim.api.global.security.jwt.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponseDTO login(LoginRequestDTO request) {
        Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // Access + Refresh 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(member.getUsername());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // Refresh 토큰 Redis에 저장
        RefreshToken token = new RefreshToken(member.getId(), refreshToken);
        refreshTokenRepository.save(token);

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    public void logout(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }
}
