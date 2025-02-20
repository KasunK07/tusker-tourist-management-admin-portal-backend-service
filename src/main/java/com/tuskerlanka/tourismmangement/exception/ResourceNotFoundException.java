package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
