package com.tuskerlanka.tourismmangement.controller;

import com.tuskerlanka.tourismmangement.constants.ApplicationConstants;
import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.AuthenticationDto;
import com.tuskerlanka.tourismmangement.dto.user.CreateUserDto;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.AuthenticationException;
import com.tuskerlanka.tourismmangement.exception.CreateUserException;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import com.tuskerlanka.tourismmangement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final static String END_POINT = "/api/v1/users";


    @PostMapping(path = "/sign-in")
    public ResponseEntity<ApiResponse> signIn(@RequestBody @Valid AuthenticationDto authenticationDto) throws AuthenticationException {
        logger.info("entering user sign in API end point...");

        AuditLog auditLog = new AuditLog();
        auditLog.setEndPoint(END_POINT+"/sign-in");
        auditLog.setMethod("POST");
        auditLog.setUsername(ApplicationConstants.USER_SYSTEM);

        Map<String, String> tokenMap = this.userService.signInUser(authenticationDto, auditLog);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", tokenMap.get("access_token"))
                .httpOnly(true)
                .secure(false) // @TODO: Set to true in production (HTTPS required)
                .sameSite("String")
                .path("/")
                .maxAge(ApplicationConstants.TokenMap.ACCESS_TOKEN_EXPIRY) // Check here is it okay if access token and cookie expiration date is same.
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokenMap.get("refresh_token"))
                .httpOnly(true)
                .secure(false)
                .sameSite("String") // @TODO: Set to true in production (HTTPS required)
                .path("/")
                .maxAge(ApplicationConstants.TokenMap.REFRESH_TOKEN_EXPIRY)
                .build();


        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }

    @PostMapping(path = "/save")
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid CreateUserDto createUserDto) throws CreateUserException {
        logger.info("entering save user API end point...");

        AuditLog auditLog = new AuditLog();
        auditLog.setEndPoint(END_POINT+"/save");
        auditLog.setMethod("POST");
        auditLog.setUsername(ApplicationConstants.USER_SYSTEM);

        this.userService.createUser(createUserDto, auditLog);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.getInstance(ResponseCode.CREATE_USER_SUCCESS));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        logger.info("entering user logout API end point...");

        AuditLog auditLog = new AuditLog();
        auditLog.setEndPoint(END_POINT+"/logout");
        auditLog.setMethod("POST");
        auditLog.setUsername(ApplicationConstants.USER_SYSTEM);

        this.userService.logoutUser(refreshToken, auditLog);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(false)
                .sameSite("String") // @TODO: Set to true in production (HTTPS required)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }
}
