package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class AuthenticationException extends BaseException {
    public AuthenticationException(ResponseCode responseCode) {
        super(responseCode);
    }
}
