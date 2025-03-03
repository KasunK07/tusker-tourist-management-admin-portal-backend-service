package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class UpdateDestinationException extends BaseException {
    public UpdateDestinationException(ResponseCode responseCode) {
        super(responseCode);
    }
}
