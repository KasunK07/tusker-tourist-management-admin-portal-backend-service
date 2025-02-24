package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class ResourceAlreadyExistsException extends BaseException {
    public ResourceAlreadyExistsException(ResponseCode responseCode) {
        super(responseCode);
    }
}
