package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.PackageImage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PackageImageRepository extends MongoRepository<PackageImage, String> {

    List<PackageImage> findAllByPackageId(String packageId);

    void deleteByImageName(String imageName);

    Optional<PackageImage> findByImageName(String imageName);
}
