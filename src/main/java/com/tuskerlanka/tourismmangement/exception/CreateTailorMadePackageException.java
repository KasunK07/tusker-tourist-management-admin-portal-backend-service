package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class CreateTailorMadePackageException extends BaseException {
    public CreateTailorMadePackageException(ResponseCode responseCode) {
        super(responseCode);
    }
}
