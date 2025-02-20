package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class UpdateDayTourException extends BaseException {
    public UpdateDayTourException(ResponseCode responseCode) {
        super(responseCode);
    }
}
