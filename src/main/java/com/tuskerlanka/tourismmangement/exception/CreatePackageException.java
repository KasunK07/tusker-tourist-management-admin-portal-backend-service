package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class CreatePackageException extends BaseException{
    public CreatePackageException(ResponseCode responseCode) {
        super(responseCode);
    }
}
