package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.AuthenticationDto;
import com.tuskerlanka.tourismmangement.dto.user.CreateUserDto;
import com.tuskerlanka.tourismmangement.dto.user.UserDto;
import com.tuskerlanka.tourismmangement.exception.AuthenticationException;
import com.tuskerlanka.tourismmangement.exception.CreateUserException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService {

    Map<String, String> signInUser(AuthenticationDto authenticationDto, AuditLog auditLog) throws AuthenticationException;

    void createUser(CreateUserDto createUserDto, AuditLog auditLog) throws CreateUserException;

    void updateUser();

    List<UserDto> findAllUsers() throws ResourceNotFoundException;

    UserDto findUserByEmail(String userEmail) throws ResourceNotFoundException;

    void logoutUser(String refreshToken, AuditLog auditLog);
}
