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
import org.springframework.transaction.annotation.Transactional;

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

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken));

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    public void logout(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }

    @Transactional
    public TokenResponseDTO reissue(String refreshTokenHeader) {
        String refreshToken = jwtTokenProvider.resolveToken(refreshTokenHeader);

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh token입니다.");
        }

        // memberId로 변경
        Long memberId = Long.valueOf(jwtTokenProvider.getSubject(refreshToken));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        RefreshToken savedToken = refreshTokenRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("저장된 refresh token이 없습니다."));

        if (!savedToken.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("refresh token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(member.getId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        savedToken.update(newRefreshToken);
        refreshTokenRepository.save(savedToken);

        return new TokenResponseDTO(newAccessToken, newRefreshToken);
    }
}
