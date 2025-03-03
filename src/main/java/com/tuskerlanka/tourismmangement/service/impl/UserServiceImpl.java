package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.document.Token;
import com.tuskerlanka.tourismmangement.document.User;
import com.tuskerlanka.tourismmangement.dto.user.AuthenticationDto;
import com.tuskerlanka.tourismmangement.dto.user.CreateUserDto;
import com.tuskerlanka.tourismmangement.dto.user.UpdateUserDto;
import com.tuskerlanka.tourismmangement.dto.user.UserDto;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.*;
import com.tuskerlanka.tourismmangement.repository.TokenRepository;
import com.tuskerlanka.tourismmangement.repository.UserRepository;
import com.tuskerlanka.tourismmangement.service.UserService;
import com.tuskerlanka.tourismmangement.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final AuthenticationManager authenticationManager;

    private final ObjectMapper mapper;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final AuditLogServiceImpl auditLogService;

    private final JwtUtil jwtUtil;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Map<String, String> signInUser(AuthenticationDto authenticationDto, AuditLog auditLog) {
        logger.info("entering sign in service implementation...");

        auditLog.setAction(AuditLogAction.AUTHENTICATE_USER);
        auditLog.setLoggedAt(LocalDateTime.now());
        try {
            Map<String, String> tokenMap = new HashMap<>();

            final String username = authenticationDto.email();
            final String rawPassword = authenticationDto.password();

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, rawPassword, null);
            Authentication authenticate = this.authenticationManager.authenticate(authenticationToken);

            if (authenticate != null && authenticate.isAuthenticated()) {
                List<String> roles = authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
                String accessToken = this.jwtUtil.generateAccessToken(username, roles);
                String refreshToken = jwtUtil.generateRefreshToken(username);

                String userId = this.userRepository.findByEmail(username).get().getId();

                this.saveRefreshToken(refreshToken, userId);

                tokenMap.put("access_token", accessToken);
                tokenMap.put("refresh_token", refreshToken);

                auditLog.setStatus(AuditLogStatus.SUCCESS);

                return tokenMap;
            } else {
                throw new AuthenticationException(ResponseCode.AUTHENTICATION_ERROR);
            }
        } catch (UsernameNotFoundException ex) {
            logger.error("error occur when user trying authenticate: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.AUTHENTICATION_ERROR.setReason(ex.getMessage());
            throw new AuthenticationException(ResponseCode.AUTHENTICATION_ERROR);
        } catch (AuthenticationException ex) {
            logger.error("error occur when user trying authenticate: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new AuthenticationException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when user trying authenticate: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new AuthenticationException(ResponseCode.INTERNAL_SERVER_ERROR);
        } finally {
            this.auditLogService.save(auditLog);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createUser(CreateUserDto createUserDto, AuditLog auditLog) throws CreateUserException {
        logger.info("entering create user service impl...");

        auditLog.setLoggedAt(LocalDateTime.now());
        auditLog.setAction(AuditLogAction.CREATE_USER);
        try {
            final String email = createUserDto.email();

            if (this.userRepository.existsByEmail(email)) {
                throw new ResourceAlreadyExistsException(ResponseCode.RECORD_ALREADY_EXISTS);
            }

            User user = User.builder()
                    .email(email)
                    .isEnabled(true)
                    .password(this.passwordEncoder.encode(createUserDto.password()))
                    .role(createUserDto.role())
                    .build();

            this.userRepository.save(user);
            logger.info("user saved in the database");

            auditLog.setStatus(AuditLogStatus.SUCCESS);
        } catch (ResourceAlreadyExistsException | CreateUserException ex) {
            logger.error("error occur when creating a user {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new CreateUserException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when creating a user {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INVALID_ARGUMENT_ERROR.setMessage(ex.getMessage());
            throw new CreateUserException((ResponseCode.INTERNAL_SERVER_ERROR));
        } finally {
            this.auditLogService.save(auditLog);
        }
    }

    @Override
    public void updateUser(String email, UpdateUserDto updateUserDto, AuditLog auditLog) throws ResourceNotFoundException,UpdateUserDetailsException {
        logger.info("entering update user service impl...");
        auditLog.setLoggedAt(LocalDateTime.now());
        auditLog.setAction(AuditLogAction.UPDATE_USER);

        try{

            Optional<User> userOptional = this.userRepository.findByEmail(email);

            if(userOptional.isEmpty()){
                logger.error("user not found");
                throw new UpdateUserDetailsException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            userOptional.get().setEmail(updateUserDto.getEmail());
            if(updateUserDto.getPassword() == null){
                userOptional.get().setPassword(this.passwordEncoder.encode(userOptional.get().getPassword()));
            }
            else{
                userOptional.get().setPassword(this.passwordEncoder.encode(updateUserDto.getPassword()));
            }
            userOptional.get().setRole(updateUserDto.getRole());
            userOptional.get().setPhone(updateUserDto.getPhone());

            this.userRepository.save(userOptional.get());
            auditLog.setStatus(AuditLogStatus.SUCCESS);
        }catch (ResourceNotFoundException ex){
            logger.error("error occur when updating user details: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new ResourceNotFoundException(ex.getResponseCode());
        }catch (Exception ex){
            logger.error("unknown error occur when updating a package: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new UpdateUserDetailsException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<UserDto> findAllUsers() throws ResourceNotFoundException {
        logger.info("entering findAllUsers service impl..");
        try {
            List<UserDto> userDtoList = new ArrayList<>();
            List<User> users = this.userRepository.findAll();
            if (users.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Users not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            for (User user : users) {
                UserDto userDto = this.mapper.convertValue(user, UserDto.class);
                userDtoList.add(userDto);
            }

            return userDtoList;
        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when fetching all users from the db: {}", ex.getResponseCode());
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when fetching all users from the db: {}", ex.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setMessage(ex.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public UserDto findUserByEmail(String userEmail) throws ResourceNotFoundException {
        logger.info("entering findAllUsers service impl..");

        try {
            Optional<User> user = this.userRepository.findByEmail(userEmail);
            if (user.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Users not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            return this.mapper.convertValue(user, UserDto.class);
        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when a user by its email address from the db: {}", ex.getResponseCode());
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when a user by its email address from the db: {}", ex.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setMessage(ex.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteUser(String email, AuditLog auditLog) throws DeleteUserException{
        logger.info("entering deleteUser service impl..");
        auditLog.setLoggedAt(LocalDateTime.now());
        auditLog.setAction(AuditLogAction.DELETE_USER);
        try{
            Optional<User> userOptional = this.userRepository.findByEmail(email);
            if(userOptional.isEmpty()){
                logger.error("user not found");
                throw new DeleteUserException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            this.userRepository.delete(userOptional.get());
        }catch (Exception ex){
            logger.error("error occur when deleting a user: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
        }finally {
            this.auditLogService.save(auditLog);
        }

    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void logoutUser(String refreshToken, AuditLog auditLog) throws InvalidTokenException {
        logger.info("entering logout user service impl...");

        auditLog.setLoggedAt(LocalDateTime.now());
        auditLog.setAction(AuditLogAction.USER_LOGOUT);
        try {
            if (refreshToken != null) {
                String jti = this.jwtUtil.extractClaim(refreshToken, claim -> claim.get("jti", String.class));
                Optional<Token> tokenOptional = this.tokenRepository.findByJti(jti);
                if (tokenOptional.isEmpty()) {
                    throw new InvalidTokenException(ResponseCode.INVALID_TOKEN);
                }

                tokenOptional.ifPresent(token -> {
                    token.revoke();
                    this.tokenRepository.save(token);
                });

                 auditLog.setStatus(AuditLogStatus.SUCCESS);
            } else {
                throw new InvalidTokenException(ResponseCode.INVALID_TOKEN);
            }
        } catch (InvalidTokenException ex) {
            logger.error("error occur when user trying to logout: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new InvalidTokenException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when user trying to logout: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new InvalidTokenException(ResponseCode.INTERNAL_SERVER_ERROR);
        } finally {
            this.auditLogService.save(auditLog);
        }
    }

    @Override
    public String renewAccessToken(String refreshToken, AuditLog auditLog) throws InvalidTokenException {
        logger.info("entering renew access token service impl...");

        auditLog.setLoggedAt(auditLog.getLoggedAt());
        auditLog.setAction(AuditLogAction.RENEW_USER_ACCESS_TOKEN);
        try {
            String jti = this.jwtUtil.extractClaim(refreshToken, claims -> claims.get("jti").toString());
            String username = this.jwtUtil.extractClaim(refreshToken, claims -> claims.get("email").toString());
            Date expirationDate = this.jwtUtil.extractClaim(refreshToken, Claims::getExpiration);

            if (expirationDate.before(new Date())) {
                throw new InvalidTokenException(ResponseCode.INVALID_TOKEN);
            }

            Optional<Token> tokenOptional = this.tokenRepository.findByJti(jti);

            if (tokenOptional.isPresent()) {
                throw new InvalidTokenException(ResponseCode.INVALID_TOKEN);
            }

            Token refreshTokenDoc = tokenOptional.get();

            if (refreshTokenDoc.getExpiryDate().before(expirationDate) || refreshTokenDoc.isRevoked()) {
                throw new InvalidTokenException(ResponseCode.INVALID_TOKEN);
            }

            String userId = refreshTokenDoc.getUserId();

            Optional<User> userOptional = this.userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                throw new InvalidTokenException(ResponseCode.INVALID_TOKEN);
            }

            String userRole = userOptional.get().getRole();

            auditLog.setStatus(AuditLogStatus.SUCCESS);

            return this.jwtUtil.generateAccessToken(username, List.of(userRole));
        } catch (InvalidTokenException ex) {
            logger.error("error occur when renewing access token: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new InvalidTokenException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when renewing access token: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new InvalidTokenException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    private void saveRefreshToken(String refreshToken, String userId) throws Exception {
        try {
            String jti = this.jwtUtil.extractClaim(refreshToken, claims -> claims.get("jti", String.class));
            Date expirationDateTime = this.jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
            Token saveRefreshToken = Token.builder()
                    .jti(jti)
                    .expiryDate(expirationDateTime)
                    .revoked(false)
                    .userId(userId)
                    .build();
            this.tokenRepository.save(saveRefreshToken);
            logger.info("refresh token successfully saved in the db");
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }
}
