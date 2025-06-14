package me.sallim.api.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.sallim.api.global.response.ApiResponse;
import me.sallim.api.global.security.exception.JwtTokenExpiredException;
import me.sallim.api.global.security.exception.JwtTokenInvalidException;
import me.sallim.api.global.security.exception.JwtTokenMissingException;
import me.sallim.api.global.security.exception.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출되는 핸들러
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        // 요청 속성에서 예외 정보 확인
        Exception exception = (Exception) request.getAttribute("exception");
        
        String errorCode;
        String message;
        HttpStatus httpStatus;

        if (exception instanceof JwtTokenMissingException) {
            errorCode = "TOKEN_MISSING";
            message = "JWT 토큰이 요청에 포함되지 않았습니다";
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (exception instanceof JwtTokenExpiredException) {
            errorCode = "TOKEN_EXPIRED";
            message = "JWT 토큰이 만료되었습니다";
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (exception instanceof JwtTokenInvalidException) {
            errorCode = "TOKEN_INVALID";
            message = "JWT 토큰이 유효하지 않습니다";
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (exception instanceof MemberNotFoundException) {
            errorCode = "MEMBER_NOT_FOUND";
            message = "사용자를 찾을 수 없습니다";
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (exception != null) {
            // 내부 서버 오류인 경우
            errorCode = "INTERNAL_SERVER_ERROR";
            message = "서버 내부 오류가 발생했습니다";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            errorCode = "UNAUTHORIZED";
            message = "인증이 필요합니다";
            httpStatus = HttpStatus.UNAUTHORIZED;
        }
        
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        ApiResponse<Object> errorResponse = ApiResponse.error(errorCode, message);
        
        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(responseBody);
    }
}
