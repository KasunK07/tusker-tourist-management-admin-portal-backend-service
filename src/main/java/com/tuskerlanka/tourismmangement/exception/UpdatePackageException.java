package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class UpdatePackageException extends BaseException {
    public UpdatePackageException(ResponseCode responseCode) {
        super(responseCode);
    }
}
