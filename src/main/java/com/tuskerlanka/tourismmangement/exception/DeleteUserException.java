package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class DeleteUserException extends BaseException {
    public DeleteUserException(ResponseCode responseCode) {
        super(responseCode);
    }
}
