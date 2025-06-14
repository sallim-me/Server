package me.sallim.api.global.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", "요청이 성공했습니다.", data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(400, code, message, null, LocalDateTime.now());
    }
    
    public static <T> ApiResponse<T> error(int status, String code, String message) {
        return new ApiResponse<>(status, code, message, null, LocalDateTime.now());
    }
}
