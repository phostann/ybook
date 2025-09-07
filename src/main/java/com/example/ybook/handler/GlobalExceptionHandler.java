package com.example.ybook.handler;

import com.example.ybook.common.ApiCode;
import com.example.ybook.common.ApiResult;
import com.example.ybook.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * <p>
 * 全局异常处理器
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResult<Void>> handleBiz(BizException ex) {
        return ResponseEntity.status(statusOf(ex.getCode()))
                .body(ApiResult.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<ApiResult<Void>> handleValidation(Exception ex) {
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
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResult.error(ApiCode.VALIDATION_ERROR, msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraint(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResult.error(ApiCode.VALIDATION_ERROR, msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleBadBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.error(ApiCode.REQUEST_NOT_READABLE, ApiCode.REQUEST_NOT_READABLE.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResult.error(ApiCode.METHOD_NOT_ALLOWED, "请求方法不被支持"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResult<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = String.format("缺少必填参数: %s", ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.error(ApiCode.PARAM_MISSING, msg));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResult<Void>> handleMissingHeader(MissingRequestHeaderException ex) {
        String msg = String.format("缺少必填请求头: %s", ex.getHeaderName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.error(ApiCode.PARAM_MISSING, msg));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResult<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("参数类型错误: %s", ex.getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.error(ApiCode.PARAM_TYPE_MISMATCH, msg));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResult.error(ApiCode.MEDIA_TYPE_NOT_SUPPORTED, ApiCode.MEDIA_TYPE_NOT_SUPPORTED.getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResult<Void>> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(ApiResult.error(ApiCode.MEDIA_TYPE_NOT_ACCEPTABLE, ApiCode.MEDIA_TYPE_NOT_ACCEPTABLE.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleNoHandler(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResult.error(ApiCode.NOT_FOUND, "资源不存在"));
    }

    @ExceptionHandler({ IllegalArgumentException.class, DataIntegrityViolationException.class })
    public ResponseEntity<ApiResult<Void>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.error(ApiCode.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleOther(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResult.error(ApiCode.INTERNAL_ERROR, ApiCode.INTERNAL_ERROR.getMessage()));
    }

    private HttpStatus statusOf(ApiCode code) {
        return switch (code) {
            case UNAUTHORIZED, TOKEN_INVALID, TOKEN_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            case MEDIA_TYPE_NOT_SUPPORTED -> HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            case MEDIA_TYPE_NOT_ACCEPTABLE -> HttpStatus.NOT_ACCEPTABLE;
            case VALIDATION_ERROR -> HttpStatus.UNPROCESSABLE_ENTITY;
            case BAD_REQUEST, PARAM_MISSING, PARAM_TYPE_MISMATCH, REQUEST_NOT_READABLE -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
