package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.DayTours;
import com.tuskerlanka.tourismmangement.document.Package;
import com.tuskerlanka.tourismmangement.document.TourImage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DayToursRepository extends MongoRepository<DayTours, String> {
    List<DayTours> findByImageIdsContaining(String imageId);
}
