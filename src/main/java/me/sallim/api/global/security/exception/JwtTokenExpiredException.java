package me.sallim.api.global.security.exception;

/**
 * JWT 토큰이 만료되었을 때 발생하는 예외
 */
public class JwtTokenExpiredException extends RuntimeException {
    
    public JwtTokenExpiredException() {
        super("JWT 토큰이 만료되었습니다");
    }
    
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
