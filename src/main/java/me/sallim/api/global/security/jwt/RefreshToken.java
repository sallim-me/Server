package me.sallim.api.global.security.jwt;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.Getter;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Getter
@RedisHash("refresh_token")
public class RefreshToken implements Serializable {

    @Id
    @Indexed
    private Long memberId;

    @Indexed
    private String refreshToken;

    private Long expiration = 86400L; // TTL: 1일 (초 단위)

    public RefreshToken(Long memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newToken) {
        this.refreshToken = newToken;
        return this;
    }
}
