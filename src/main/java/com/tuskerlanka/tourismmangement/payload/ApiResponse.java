package com.tuskerlanka.tourismmangement.payload;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Getter
public class ApiResponse {
    private int code;
    private String message;
    private String reason;
    private Date timeStamp;
    private Object data;

    public ApiResponse(int code, String message, String reason) {
        this.code = code;
        this.message = message;
        this.reason = reason;
        this.timeStamp = new Date();
    }

    public ApiResponse(int code, String message, String reason, Object date) {
        this.code = code;
        this.message = message;
        this.reason = reason;
        this.timeStamp = new Date();
        this.data = date;
    }

    public static ApiResponse getInstance(ResponseCode responseCode, Object data) {
        return new ApiResponse(responseCode.getStatusCode(), responseCode.getMessage(), responseCode.getReason(), data);
    }

    public static ApiResponse getInstance(ResponseCode responseCode) {
        return new ApiResponse(responseCode.getStatusCode(), responseCode.getMessage(), responseCode.getReason());
    }
}
