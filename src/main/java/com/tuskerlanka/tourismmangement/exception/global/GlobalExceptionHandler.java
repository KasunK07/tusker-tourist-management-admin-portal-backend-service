package com.tuskerlanka.tourismmangement.exception.global;

import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.CreatePackageException;
import com.tuskerlanka.tourismmangement.exception.CreateUserException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import com.tuskerlanka.tourismmangement.exception.UpdatePackageException;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(fieldError -> {
            String fieldName;
            if (fieldError instanceof FieldError) {
                fieldName = ((FieldError) fieldError).getField();
            } else {
                fieldName = fieldError.getObjectName();
            }
            errorMap.put("field", fieldName);
            errorMap.put("message", fieldError.getDefaultMessage());
            LOGGER.error("{}", fieldError.getCode());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.getInstance(ResponseCode.INVALID_ARGUMENT_ERROR, errorMap));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        LOGGER.error("error occur in handleValidationExceptions: {}", ex.getMessage());
        AtomicReference<String> reason = new AtomicReference<>("");
        ex.getConstraintViolations().forEach(constraintViolation -> reason.set(constraintViolation.getMessage()));
        ResponseCode.INVALID_ARGUMENT_ERROR.setReason(reason.get());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.getInstance(ResponseCode.INVALID_ARGUMENT_ERROR, null));
    }

    @ExceptionHandler(CreatePackageException.class)
    public ResponseEntity<ApiResponse> handleCreatePackageException(CreatePackageException ex) {
        LOGGER.error("error occur in handleResourceNotFoundException: {}", ex.getResponseCode());
        return ResponseEntity.status(ex.getResponseCode().getHttpStatus()).body(ApiResponse.getInstance(ex.getResponseCode(), null));
    }


    @ExceptionHandler(CreateUserException.class)
    public ResponseEntity<ApiResponse> handleCreateUserException(CreateUserException ex) {
        LOGGER.error("error occur in handleResourceNotFoundException: {}", ex.getResponseCode());
        return ResponseEntity.status(ex.getResponseCode().getHttpStatus()).body(ApiResponse.getInstance(ex.getResponseCode(), null));
    }

    @ExceptionHandler(UpdatePackageException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(UpdatePackageException ex) {
        LOGGER.error("error occur in handleResourceNotFoundException: {}", ex.getResponseCode());
        return ResponseEntity.status(ex.getResponseCode().getHttpStatus()).body(ApiResponse.getInstance(ex.getResponseCode(), null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        LOGGER.error("error occur in handleResourceNotFoundException: {}", ex.getResponseCode());
        return ResponseEntity.status(ex.getResponseCode().getHttpStatus()).body(ApiResponse.getInstance(ex.getResponseCode(), null));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        LOGGER.error("error occur handleExceptionInternal: {}", ex.getMessage());
        ResponseCode.AUTHENTICATION_ERROR.setReason(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.getInstance(ResponseCode.AUTHENTICATION_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptionInternal(Exception ex) {
        LOGGER.error("unknown error occur: {}", ex.getMessage());
        ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
