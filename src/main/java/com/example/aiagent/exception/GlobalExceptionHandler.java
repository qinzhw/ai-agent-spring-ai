package com.example.aiagent.exception;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一捕获异常并返回 BaseResponse，同时设置符合 RESTful 语义的 HTTP 状态码。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<?>> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(ResultUtils.error(e.getCode(), e.getMessage()));
    }

    /**
     * 参数校验异常（@Valid 触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return ResponseEntity.badRequest()
                .body(ResultUtils.error(ErrorCode.PARAMS_ERROR, message));
    }

    /**
     * 缺少必填请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<?>> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.warn("缺少必填请求参数: {}", e.getParameterName());
        return ResponseEntity.badRequest()
                .body(ResultUtils.error(ErrorCode.PARAMS_ERROR, "缺少必填参数: " + e.getParameterName()));
    }

    /**
     * 请求体不可读（JSON 格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<?>> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败", e);
        return ResponseEntity.badRequest()
                .body(ResultUtils.error(ErrorCode.PARAMS_ERROR, "请求体格式错误"));
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<?>> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMethod());
        return ResponseEntity.status(org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResultUtils.error(ErrorCode.PARAMS_ERROR, "不支持的请求方法: " + e.getMethod()));
    }

    /**
     * 404 未找到
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<?>> noHandlerFoundExceptionHandler(NoHandlerFoundException e) {
        log.warn("接口不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                .body(ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "接口不存在"));
    }

    /**
     * 兜底运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<?>> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResponseEntity.status(ErrorCode.SYSTEM_ERROR.getHttpStatus())
                .body(ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统异常"));
    }

}
