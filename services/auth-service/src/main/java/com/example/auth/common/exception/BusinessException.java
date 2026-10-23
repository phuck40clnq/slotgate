package com.example.auth.common.exception;

import java.util.Map;

import com.example.auth.common.error.AuthBusinessErrorCode;

public class BusinessException
        extends BaseException {
    
    public BusinessException(AuthBusinessErrorCode e) {
        super(e);
    }
    
    public BusinessException(AuthBusinessErrorCode e, Map<String, Object> params) {
        super(e, params);
    }

}
