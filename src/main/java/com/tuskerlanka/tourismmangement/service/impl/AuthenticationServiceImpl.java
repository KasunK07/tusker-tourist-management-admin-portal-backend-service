package com.tuskerlanka.tourismmangement.service.impl;

import com.tuskerlanka.tourismmangement.document.Token;
import com.tuskerlanka.tourismmangement.dto.AuthenticationDto;
import com.tuskerlanka.tourismmangement.repository.TokenRepository;
import com.tuskerlanka.tourismmangement.service.AuthenticationService;
import com.tuskerlanka.tourismmangement.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final TokenRepository tokenRepository;

    @Override
    public Map<String, String> login(AuthenticationDto authenticationDto) {
        logger.info("entering login service impl..");

        Map<String, String> tokenMap = new HashMap<>();
        final String username = authenticationDto.username();;

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(username, authenticationDto.password(), null);
        Authentication authenticateResponse = authenticationManager.authenticate(authentication);

        if (authenticateResponse != null && authenticateResponse.isAuthenticated()) {
            final String authorities = authenticateResponse.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
            // final String accessToken = this.jwtUtil.generateAccessToken(username, authorities);
            final String refreshToken = this.jwtUtil.generateRefreshToken(username);
            
            this.saveRefreshToken(refreshToken);

            tokenMap.put("access_token", null);
            tokenMap.put("refresh_token", refreshToken);

            return tokenMap;
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            String refreshJti = this.jwtUtil.extractClaim(refreshToken, claims -> claims.get("jti", String.class));
            Optional<Token> tokenOptional = this.tokenRepository.findByJti(refreshJti);
            tokenOptional.ifPresent(token -> {
                token.revoke();
                this.tokenRepository.save(token);
            });
        }
    }

    private void saveRefreshToken(String refreshToken) {
        Date date = this.jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
        String jti = this.jwtUtil.extractClaim(refreshToken, claims -> claims.get("jti", String.class));

        Token tokenDocument = Token.builder()
                .expiryDate(date)
                .jti(jti)
                .build();

        this.tokenRepository.save(tokenDocument);
    }
}
