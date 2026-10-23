package com.example.user.common.exception;

import java.util.Map;

import com.example.user.common.error.UserInfrasErrorCode;

public class InfrastructureException
        extends BaseException {

    public InfrastructureException(UserInfrasErrorCode e) {
        super(e);
    }

    public InfrastructureException(UserInfrasErrorCode e, Throwable cause) {
        super(e, cause);
    }

    public InfrastructureException(UserInfrasErrorCode e, Map<String, Object> params) {
        super(e, params);
    }

    public InfrastructureException(UserInfrasErrorCode e, Map<String, Object> params, Throwable cause) {
        super(e, params, cause);
    }

}
