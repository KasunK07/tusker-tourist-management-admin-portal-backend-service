package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class CreateDayTourException extends BaseException {
    public CreateDayTourException(ResponseCode responseCode) {
        super(responseCode);
    }
}
