package me.sallim.api.global.security.exception;

/**
 * 사용자가 존재하지 않을 때 발생하는 예외
 */
public class MemberNotFoundException extends RuntimeException {
    
    public MemberNotFoundException() {
        super("사용자를 찾을 수 없습니다");
    }
    
    public MemberNotFoundException(String message) {
        super(message);
    }
}
