package me.sallim.api.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", "요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(400, code, message, null);
    }
}
