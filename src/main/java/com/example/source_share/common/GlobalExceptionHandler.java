package com.example.source_share.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * 负责统一捕获 Controller 抛出的异常，记录日志并返回友好提示
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 捕获 IllegalArgumentException (通常是参数错误或业务逻辑校验失败)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        logger.warn("请求参数错误: {} - URL: {}", e.getMessage(), request.getRequestURI());
        return Result.error(400, e.getMessage());
    }

    /**
     * 捕获所有其他未处理的异常 (Exception)
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        // 记录完整的堆栈信息，方便排查
        logger.error("系统内部错误 - URL: {}", request.getRequestURI(), e);
        return Result.error(500, "服务器出了点小差错，请稍后再试");
    }
}
