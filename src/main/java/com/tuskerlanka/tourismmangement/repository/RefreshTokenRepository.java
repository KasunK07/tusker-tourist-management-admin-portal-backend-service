package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByJti(String jti);

    boolean existsByJtiAndRevokedTrue(String jti);

    void deleteByJti(String jti);
}
