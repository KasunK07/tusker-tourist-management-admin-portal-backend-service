package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.PackageImage;
import com.tuskerlanka.tourismmangement.document.TourImage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DayToursImageRepository extends MongoRepository<TourImage , String>{

    Optional<TourImage> findByImageName(String imageName);
}
