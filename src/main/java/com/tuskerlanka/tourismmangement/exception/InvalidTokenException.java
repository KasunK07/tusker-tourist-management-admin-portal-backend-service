package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException(ResponseCode responseCode) {
        super(responseCode);
    }
}
