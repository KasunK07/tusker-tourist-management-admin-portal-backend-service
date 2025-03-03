package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query(value = "{ 'email' : ?0 }", fields = "{ '_id' : 1 }")
    Optional<String> findIdByEmail(String email);

    boolean existsByEmail(String email);
}
