package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.Package;
import com.tuskerlanka.tourismmangement.document.PackageImage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PackageRepository extends MongoRepository<Package, String> {

    List<Package> findByImageIdsContaining(String imageId);

}
