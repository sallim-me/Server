package me.sallim.api.global.security.exception;

/**
 * JWT 토큰이 요청에 포함되지 않았을 때 발생하는 예외
 */
public class JwtTokenMissingException extends RuntimeException {
    
    public JwtTokenMissingException() {
        super("JWT 토큰이 요청에 포함되지 않았습니다");
    }
    
    public JwtTokenMissingException(String message) {
        super(message);
    }
}
