package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.DayTours;
import com.tuskerlanka.tourismmangement.document.Destinations;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface DestinationRepository extends MongoRepository<Destinations, String> {

    List<Destinations> findByImageIdsContaining(String imageId);
}
