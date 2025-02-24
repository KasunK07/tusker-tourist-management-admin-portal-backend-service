package com.tuskerlanka.tourismmangement.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    SUCCESS("", "", null, 0),
    ERROR("", "", null, 0),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "", HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value()),
    INVALID_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Validation Error", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value()),

    CREATE_USER_SUCCESS("User created successfully", "", HttpStatus.CREATED, HttpStatus.CREATED.value()),
    CREATE_USER_ERROR("User creation failed", "", null, 0),

    AUTHENTICATION_SUCCESS("User successfully authenticated", "", HttpStatus.OK, HttpStatus.NO_CONTENT.value()),
    AUTHENTICATION_ERROR("Unsuccessful authentication", HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value() ),

    PACKAGE_CREATED_SUCCESSFULLY("Package created successfully", "", HttpStatus.CREATED, HttpStatus.CREATED.value()),
    PACKAGE_CREATE_ERROR("Package creation failed", "", null, 0),
    PACKAGE_UPDATED_SUCCESSFULLY("Package updated successfully", "", HttpStatus.CREATED, HttpStatus.CREATED.value()),
    PACKAGE_UPDATE_ERROR("Package update failed", "", HttpStatus.CREATED, HttpStatus.CREATED.value()),
    PACKAGE_DELETED_SUCCESSFULLY("Package deleted successfully", "", HttpStatus.OK, HttpStatus.OK.value()),
    PACKAGE_DELETE_ERROR("Package deletion failed", "", HttpStatus.OK, HttpStatus.OK.value()),

    DATA_RETRIEVED_SUCCESS("Data retrieved successfully", "", HttpStatus.OK, HttpStatus.OK.value()),
    DATA_RETRIEVED_ERROR("Failed to retrieve data", "", HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value()),

    DAY_TOUR_CREATED_SUCCESSFULLY("Day tour created successfully", "", HttpStatus.CREATED, HttpStatus.CREATED.value()),
    DAY_TOUR_CREATE_ERROR("Day tour creation failed", "", null, 0),
    DAY_TOUR_DELETED_SUCCESSFULLY("Day tour deleted successfully", "", HttpStatus.OK, HttpStatus.OK.value()),
    DAY_TOUR_DELETE_ERROR("Day tour deletion failed", "", HttpStatus.OK, HttpStatus.OK.value());

    RECORD_DOES_NOT_EXIST("No such record found", "", HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value()),
    RECORD_ALREADY_EXISTS("The record already exists", "", HttpStatus.CONFLICT, HttpStatus.CONFLICT.value()),

    INVALID_TOKEN("Unauthorized", "The provided token is invalid or has expired. Please log in again.", HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value());

    private String message;
    private String reason;
    @JsonIgnore
    private HttpStatus httpStatus;
    private int statusCode;

    ResponseCode(String message, String reason, HttpStatus httpStatus, int statusCode) {
        this.message = message;
        this.reason = reason;
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
