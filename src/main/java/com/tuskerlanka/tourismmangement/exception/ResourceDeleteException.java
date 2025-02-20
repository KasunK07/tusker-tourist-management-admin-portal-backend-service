package com.tuskerlanka.tourismmangement.exception;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;

public class ResourceDeleteException extends BaseException {
    public ResourceDeleteException(ResponseCode responseCode) {
        super(responseCode);
    }
}
