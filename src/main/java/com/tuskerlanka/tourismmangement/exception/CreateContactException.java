package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class CreateContactException extends BaseException {
    public CreateContactException(ResponseCode responseCode) {
        super(responseCode);
    }
}
