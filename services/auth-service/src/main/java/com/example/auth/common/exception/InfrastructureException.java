package com.example.auth.common.exception;

import java.util.Map;

import com.example.auth.common.error.AuthInfrasErrorCode;

public class InfrastructureException
        extends BaseException {

    public InfrastructureException(AuthInfrasErrorCode e) {
        super(e);
    }

    public InfrastructureException(AuthInfrasErrorCode e, Throwable cause) {
        super(e, cause);
    }

    public InfrastructureException(AuthInfrasErrorCode e, Map<String, Object> params) {
        super(e, params);
    }

    public InfrastructureException(AuthInfrasErrorCode e, Map<String, Object> params, Throwable cause) {
        super(e, params, cause);
    }

}
