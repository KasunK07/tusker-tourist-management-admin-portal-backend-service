package com.tuskerlanka.tourismmangement.controller;

import com.tuskerlanka.tourismmangement.dto.AuthenticationDto;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import com.tuskerlanka.tourismmangement.service.AuthenticationService;
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
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationService authenticationService;

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthenticationDto authenticationDto) {
        logger.info("entering login api end point...");
        Map<String, String> response = this.authenticationService.login(authenticationDto);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", response.get("access_token"))
                .httpOnly(true)
                .secure(false) // @TODO: Set to true in production (HTTPS required)
                .sameSite("String")
                .path("/")
                .maxAge(20 * 60) // 20 minutes
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", response.get("refresh_token"))
                .httpOnly(true)
                .secure(false) // @TODO: Set to true in production (HTTPS required)
                .sameSite("String")
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.getInstance(ResponseCode.AUTHENTICATION_SUCCESS));
    }

    /*
    @PostMapping(path = "/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {

    }

     */

    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        this.authenticationService.logout(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // @TODO: Set to true in production (HTTPS required)
                .sameSite("String")
                .path("/")
                .maxAge(0) // 7 days
                .build();

        ResponseCode.SUCCESS.setMessage("Logged out successfully");

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.getInstance(ResponseCode.SUCCESS));
    }
}
