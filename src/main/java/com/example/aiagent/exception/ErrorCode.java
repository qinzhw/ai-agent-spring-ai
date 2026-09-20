package com.example.aiagent.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SUCCESS(0, "ok", HttpStatus.OK),
    PARAMS_ERROR(40000, "请求参数错误", HttpStatus.BAD_REQUEST),
    NOT_LOGIN_ERROR(40100, "未登录", HttpStatus.UNAUTHORIZED),
    NO_AUTH_ERROR(40101, "无权限", HttpStatus.FORBIDDEN),
    NOT_FOUND_ERROR(40400, "请求数据不存在", HttpStatus.NOT_FOUND),
    FORBIDDEN_ERROR(40300, "禁止访问", HttpStatus.FORBIDDEN),
    SYSTEM_ERROR(50000, "系统内部异常", HttpStatus.INTERNAL_SERVER_ERROR),
    OPERATION_ERROR(50001, "操作失败", HttpStatus.INTERNAL_SERVER_ERROR);

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    /**
     * 对应的 HTTP 状态码
     */
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}

