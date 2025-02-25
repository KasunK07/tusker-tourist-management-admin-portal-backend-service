package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.Destinations;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface DestinationRepository extends MongoRepository<Destinations, String> {
}
