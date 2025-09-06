package com.example.ybook.handler;

import com.example.ybook.common.ApiCode;
import com.example.ybook.common.ApiResponse;
import com.example.ybook.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBiz(BizException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ApiResponse<Void> handleValidation(Exception ex) {
        String msg;
        if (ex instanceof MethodArgumentNotValidException manve) {
            msg = manve.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        } else if (ex instanceof BindException be) {
            msg = be.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
        } else {
            msg = ApiCode.VALIDATION_ERROR.getMessage();
        }
        return ApiResponse.error(ApiCode.VALIDATION_ERROR, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraint(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return ApiResponse.error(ApiCode.VALIDATION_ERROR, msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleBadBody(HttpMessageNotReadableException ex) {
        return ApiResponse.error(ApiCode.REQUEST_NOT_READABLE, ApiCode.REQUEST_NOT_READABLE.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Void> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ApiResponse.error(ApiCode.METHOD_NOT_ALLOWED, "请求方法不被支持");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse<Void> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = String.format("缺少必填参数: %s", ex.getParameterName());
        return ApiResponse.error(ApiCode.PARAM_MISSING, msg);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ApiResponse<Void> handleMissingHeader(MissingRequestHeaderException ex) {
        String msg = String.format("缺少必填请求头: %s", ex.getHeaderName());
        return ApiResponse.error(ApiCode.PARAM_MISSING, msg);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("参数类型错误: %s", ex.getName());
        return ApiResponse.error(ApiCode.PARAM_TYPE_MISMATCH, msg);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResponse<Void> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return ApiResponse.error(ApiCode.MEDIA_TYPE_NOT_SUPPORTED, ApiCode.MEDIA_TYPE_NOT_SUPPORTED.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ApiResponse<Void> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return ApiResponse.error(ApiCode.MEDIA_TYPE_NOT_ACCEPTABLE, ApiCode.MEDIA_TYPE_NOT_ACCEPTABLE.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResponse<Void> handleNoHandler(NoHandlerFoundException ex) {
        return ApiResponse.error(ApiCode.NOT_FOUND, "资源不存在");
    }

    @ExceptionHandler({ IllegalArgumentException.class, DataIntegrityViolationException.class })
    public ApiResponse<Void> handleBadRequest(RuntimeException ex) {
        return ApiResponse.error(ApiCode.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Object handleOther(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred", ex);
        return ApiResponse.error(ApiCode.INTERNAL_ERROR, ApiCode.INTERNAL_ERROR.getMessage());
    }
}
