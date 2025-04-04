package me.sallim.api.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long ACCESS_EXP = 1000L * 60 * 30;       // 30분
    private final long REFRESH_EXP = 1000L * 60 * 60 * 24; // 24시간

    public String createAccessToken(String username) {
        return createToken(username, ACCESS_EXP);
    }

    public String createRefreshToken() {
        return createToken(UUID.randomUUID().toString(), REFRESH_EXP);
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
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}