package me.sallim.api.global.security.exception;

/**
 * JWT 토큰이 유효하지 않을 때 발생하는 예외
 */
public class JwtTokenInvalidException extends RuntimeException {
    
    public JwtTokenInvalidException() {
        super("JWT 토큰이 유효하지 않습니다");
    }
    
    public JwtTokenInvalidException(String message) {
        super(message);
    }
}
