package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.DestinationImage;
import com.tuskerlanka.tourismmangement.document.TourImage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DestinationImageRepository extends MongoRepository<DestinationImage, String> {

    Optional<DestinationImage> findByImageName(String imageName);
}
