package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.DayTours;
import com.tuskerlanka.tourismmangement.document.TourImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DayToursRepository extends MongoRepository<DayTours, String> {

}
