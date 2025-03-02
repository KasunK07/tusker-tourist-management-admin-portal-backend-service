package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class UpdateUserDetailsException extends BaseException {
    public UpdateUserDetailsException(ResponseCode responseCode) {
        super(responseCode);
    }
}
