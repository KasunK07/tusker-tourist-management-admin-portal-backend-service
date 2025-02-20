package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class BaseException extends Exception {

    private final ResponseCode responseCode;
    public BaseException(ResponseCode responseCode) {
        super();
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}
