package com.example.common.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class APIResponse<T> {
    private int code;
    private String message;
    private T data;
    private String requestId;

    public APIResponse(int code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public APIResponse(int code, String message, T data, String requestId){
        this.code = code;
        this.message = message;
        this.data = data;
        this.requestId = requestId;
    }

    public static <T> APIResponse<T> ok(String message, T data) {
        return new APIResponse<>(HttpStatus.OK.value(), message, data);
    }
    public static <T> APIResponse<T> ok(T data) {
        return new APIResponse<>(HttpStatus.OK.value(), null, data);
    }

    public static <T> APIResponse<T> ok(String message, T data, String requestId) {
        return new APIResponse<>(HttpStatus.OK.value(), message, data, requestId);
    }

    public static <T> APIResponse<T> error(int code, String message) {
        return new APIResponse<>(code, message, null);
    }

    public static <T> APIResponse<T> error(int code, String message, T data) {
        return new APIResponse<>(code, message, data);
    }
}
