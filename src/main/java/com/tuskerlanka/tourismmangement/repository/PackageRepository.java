package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.Package;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PackageRepository extends MongoRepository<Package, String> {
}
