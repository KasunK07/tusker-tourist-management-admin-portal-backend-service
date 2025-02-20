package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.dto.AuthenticationDto;

import java.util.Map;

public interface AuthenticationService {

    Map<String, String> login(AuthenticationDto authenticationDto);

    void logout(String refreshToken);
}
