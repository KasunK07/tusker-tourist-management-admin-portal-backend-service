package com.tuskerlanka.tourismmangement.controller;

import com.tuskerlanka.tourismmangement.document.User;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import com.tuskerlanka.tourismmangement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping(path = "/save")
    public ResponseEntity<ApiResponse> create(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.getInstance(ResponseCode.SUCCESS));
    }
}
