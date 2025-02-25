package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.DestinationImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DestinationImageRepository extends MongoRepository<DestinationImage, String> {
}
