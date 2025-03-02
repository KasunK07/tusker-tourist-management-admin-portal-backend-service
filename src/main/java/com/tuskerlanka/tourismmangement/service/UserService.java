package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.AuthenticationDto;
import com.tuskerlanka.tourismmangement.dto.user.CreateUserDto;
import com.tuskerlanka.tourismmangement.dto.user.UpdateUserDto;
import com.tuskerlanka.tourismmangement.dto.user.UserDto;
import com.tuskerlanka.tourismmangement.exception.*;

import java.util.List;
import java.util.Map;

public interface UserService {

    Map<String, String> signInUser(AuthenticationDto authenticationDto, AuditLog auditLog) throws AuthenticationException;

    void createUser(CreateUserDto createUserDto, AuditLog auditLog) throws CreateUserException;

    void updateUser(String email, UpdateUserDto updateUserDto, AuditLog auditLog) throws ResourceNotFoundException, UpdateUserDetailsException;

    List<UserDto> findAllUsers() throws ResourceNotFoundException;

    UserDto findUserByEmail(String userEmail) throws ResourceNotFoundException;

    void deleteUser(String email,AuditLog auditLog) throws DeleteUserException;

    void logoutUser(String refreshToken, AuditLog auditLog);
}
