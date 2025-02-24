package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {
    Optional<Token> findByJti(String jti);

    boolean existsByJtiAndRevokedTrue(String jti);

    void deleteByJti(String jti);
}
