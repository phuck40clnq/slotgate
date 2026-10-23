package com.example.user.common.exception;

import java.util.Map;

import com.example.user.common.error.UserBusinessErrorCode;

public class BusinessException
        extends BaseException {
    
    public BusinessException(UserBusinessErrorCode e) {
        super(e);
    }
    
    public BusinessException(UserBusinessErrorCode e, Map<String, Object> params) {
        super(e, params);
    }

}
