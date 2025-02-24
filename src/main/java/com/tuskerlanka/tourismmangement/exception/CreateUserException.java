package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class CreateUserException extends BaseException {

    public CreateUserException(ResponseCode responseCode) {
        super(responseCode);
    }
}
