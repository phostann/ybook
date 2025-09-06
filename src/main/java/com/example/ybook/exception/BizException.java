package com.example.ybook.exception;

import com.example.ybook.common.ApiCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final ApiCode code;

    public BizException(ApiCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public BizException(ApiCode code, String message) {
        super(message);
        this.code = code;
    }
}

