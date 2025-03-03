package com.tuskerlanka.tourismmangement.util;

import com.tuskerlanka.tourismmangement.constants.ApplicationConstants;
import com.tuskerlanka.tourismmangement.constants.ApplicationConstants.TokenMap;
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
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.tuskerlanka.tourismmangement.constants.ApplicationConstants.TokenMap.*;

@Service
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateAccessToken(String username, List<String> authorities) {
        return Jwts.builder()
                .issuer(TOKEN_ISSUER)
                .subject(TOKEN_SUBJECT)
                .claim("email", username)
                .claim("authorities", authorities)
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(this.getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .issuer(TOKEN_ISSUER)
                .subject(TOKEN_SUBJECT)
                .claim("type", "refresh")
                .claim("email", username)
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
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
