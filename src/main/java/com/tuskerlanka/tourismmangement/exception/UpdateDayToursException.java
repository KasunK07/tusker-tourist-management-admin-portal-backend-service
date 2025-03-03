package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class UpdateDayToursException extends BaseException {
    public UpdateDayToursException(ResponseCode responseCode) {
        super(responseCode);
    }
}
