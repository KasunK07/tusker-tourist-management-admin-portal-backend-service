package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class CreateDestinationException extends BaseException {
    public CreateDestinationException(ResponseCode responseCode) {
        super(responseCode);
    }
}
