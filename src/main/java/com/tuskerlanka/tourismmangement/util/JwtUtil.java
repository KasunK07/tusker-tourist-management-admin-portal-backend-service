package com.tuskerlanka.tourismmangement.util;

import com.tuskerlanka.tourismmangement.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateAccessToken(String username, String authorities) {
        return Jwts.builder()
                .issuer(ApplicationConstants.TOKEN_ISSUER)
                .subject(ApplicationConstants.TOKEN_SUBJECT)
                .claim("username", username)
                .claim("authorities", authorities)
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ApplicationConstants.ACCESS_TOKEN_EXPIRY))
                .signWith(this.getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .issuer(ApplicationConstants.TOKEN_ISSUER)
                .subject(ApplicationConstants.TOKEN_SUBJECT)
                .claim("type", "refresh")
                .claim("username", username)
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ApplicationConstants.REFRESH_TOKEN_EXPIRY))
                .signWith(this.getSigningKey())
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token) throws JwtException {
        try {
            Claims claims = this.getParser().parseSignedClaims(token).getPayload();
            return !this.isTokenExpired(claims);
        } catch (JwtException ex) {
            throw new JwtException(ex.getMessage());
        }
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return getParser().parseSignedClaims(token).getPayload();
    }

    private JwtParser getParser() {
        return Jwts.parser().verifyWith(this.getSigningKey()).build();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(this.secretKey));
    }
}
