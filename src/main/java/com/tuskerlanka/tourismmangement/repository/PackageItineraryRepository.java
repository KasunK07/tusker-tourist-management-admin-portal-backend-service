package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.PackageItinerary;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PackageItineraryRepository extends MongoRepository<PackageItinerary, String> {
}
