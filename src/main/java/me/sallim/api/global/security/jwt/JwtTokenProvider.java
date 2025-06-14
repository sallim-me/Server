package me.sallim.api.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import me.sallim.api.global.security.exception.JwtTokenExpiredException;
import me.sallim.api.global.security.exception.JwtTokenInvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Value("${spring.jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    @Value("${spring.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    public String createAccessToken(Long memberId) {
        return createToken(memberId.toString(), accessTokenValidityInSeconds * 1000);
    }

    public String createRefreshToken(Long memberId) {
        return createToken(memberId.toString(), refreshTokenValidityInSeconds * 1000);
    }
    private String createToken(String subject, long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰을 검증하고 실패 시 구체적인 예외를 발생시킵니다.
     */
    public void validateTokenWithException(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException("JWT 토큰이 만료되었습니다");
        } catch (MalformedJwtException | io.jsonwebtoken.security.SignatureException | UnsupportedJwtException e) {
            throw new JwtTokenInvalidException("JWT 토큰이 유효하지 않습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JwtTokenInvalidException("JWT 토큰이 비어있거나 잘못된 형식입니다");
        }
    }

    public String resolveToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}