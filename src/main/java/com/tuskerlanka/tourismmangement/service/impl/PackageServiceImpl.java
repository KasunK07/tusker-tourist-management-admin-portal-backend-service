package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.document.Package;
import com.tuskerlanka.tourismmangement.document.PackageImage;
import com.tuskerlanka.tourismmangement.document.PackageItinerary;
import com.tuskerlanka.tourismmangement.dto.*;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.PackageStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.CreatePackageException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import com.tuskerlanka.tourismmangement.exception.UpdatePackageException;
import com.tuskerlanka.tourismmangement.repository.PackageImageRepository;
import com.tuskerlanka.tourismmangement.repository.PackageItineraryRepository;
import com.tuskerlanka.tourismmangement.repository.PackageRepository;
import com.tuskerlanka.tourismmangement.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final static Logger logger = LoggerFactory.getLogger(PackageRepository.class);

    private final ObjectMapper mapper;

    private final PackageRepository packageRepository;

    private final PackageImageRepository packageImageRepository;

    private final PackageItineraryRepository packageItineraryRepository;

    private final AuditLogServiceImpl auditLogService;

    @Value("${package.image.folder.path}")
    private String imageFolderPath;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    // Is it a one image or multiple images - ask from Kasun
    public void save(CreatePackageDto createPackageDto, MultipartFile files[], AuditLog auditLog) throws CreatePackageException {
        logger.info("entering save package service impl...");
        auditLog.setAction(AuditLogAction.ADD_NEW_PACKAGE);
        auditLog.setLoggedAt(LocalDateTime.now());
        try {
            logger.info("{}", createPackageDto);

            Set<String> imageIdSet = new HashSet<>();
            Set<String> packageItineraryIds = new HashSet<>();
            List<CreateItineraryDto> itineraryDetails = createPackageDto.getItineraryDetails();

            Package newPackage = this.mapper.convertValue(createPackageDto, Package.class);
            newPackage.setStatus(PackageStatus.PACKAGE_ACTIVE);
            newPackage.setCreatedAt(LocalDateTime.now());

            Package savedPackage = this.packageRepository.save(newPackage);
            logger.info("new package successfully added to the db");

            final String packageId = savedPackage.getId();

            this.saveItineraryDetails(itineraryDetails, packageId, packageItineraryIds);
            this.savePackageImages(files, packageId, imageIdSet);

            savedPackage.setImageIds(imageIdSet);
            savedPackage.setItineraryIds(packageItineraryIds);
            this.packageRepository.save(savedPackage);
            logger.info("package image ids are successfully saved with package");

            auditLog.setStatus(AuditLogStatus.SUCCESS);
        } catch (Exception ex) {
            logger.error("unknown error occur when creating a new package: {}", ex.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new CreatePackageException(ResponseCode.INTERNAL_SERVER_ERROR);
        } finally {
            this.auditLogService.save(auditLog);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    // Is it a one image or multiple images - ask from Kasun
    public void updatePackage(String packageId, UpdatePackageDto updatePackageDto, MultipartFile file, AuditLog auditLog) throws ResourceNotFoundException, UpdatePackageException {
        logger.info("entering update package service impl...");
        auditLog.setAction(AuditLogAction.UPDATE_PACKAGE);
        auditLog.setLoggedAt(LocalDateTime.now());
        try {
            Optional<Package> packageOptional = this.packageRepository.findById(packageId);

            if (packageOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Package is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            packageOptional.get().setName(updatePackageDto.getName());
            packageOptional.get().setDescription(updatePackageDto.getDescription());
            packageOptional.get().setPrice(updatePackageDto.getPrice());
            packageOptional.get().setStatus(updatePackageDto.getStatus());
            packageOptional.get().setUpdatedAt(LocalDateTime.now());

            this.packageRepository.save(packageOptional.get());

        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when updating a package: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when updating a package: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new UpdatePackageException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<PackageResponseDto> findAll() throws ResourceNotFoundException {
        logger.info("entering find all packages service impl...");
        try {
            List<PackageResponseDto> packageResponseDtoList = new ArrayList<>();
            List<Package> packageList = this.packageRepository.findAll();

            if (packageList.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("No packages available at the moment");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            for (Package packageRecord : packageList) {
                List<ItineraryResponseDto> itineraryResponseList = new ArrayList<>();
                List<PackageImageResponseDto> imageResponseList = new ArrayList<>();
                PackageResponseDto packageResponseDto = new PackageResponseDto();
                packageResponseDto.setId(packageRecord.getId());
                packageResponseDto.setName(packageRecord.getName());
                packageResponseDto.setDescription(packageRecord.getDescription());
                packageResponseDto.setStatus(packageRecord.getStatus());
                packageResponseDto.setPrice(packageRecord.getPrice());
                packageResponseDto.setHighlights(packageRecord.getHighlights());
                Set<String> imageIds = packageRecord.getImageIds();
                Set<String> itineraryIds = packageRecord.getItineraryIds();

                for (String id: imageIds) {
                    Optional<PackageImage> imageById = this.packageImageRepository.findById(id);
                    PackageImageResponseDto packageImageResponseDto = new PackageImageResponseDto();
                    packageImageResponseDto.setImageName(imageById.get().getImageName());
                    packageImageResponseDto.setImagePath(imageById.get().getImageType());
                    packageImageResponseDto.setMimeType(imageById.get().getMimeType());
                    packageImageResponseDto.setImageData(this.getImageData(imageById.get().getImagePath()));
                    imageResponseList.add(packageImageResponseDto);
                }
                packageResponseDto.setPackageImageDetails(imageResponseList);

                for(String itineraryId: itineraryIds) {
                    Optional<PackageItinerary> itinerary = this.packageItineraryRepository.findById(itineraryId);
                    ItineraryResponseDto itineraryResponseDto = ItineraryResponseDto.builder()
                            .description(itinerary.get().getDescription())
                            .heading(itinerary.get().getHeading())
                            .startDate(itinerary.get().getStartDate())
                            .endDate(itinerary.get().getEndDate())
                            .build();
                    itineraryResponseList.add(itineraryResponseDto);
                }
                packageResponseDto.setItineraryDetails(itineraryResponseList);
                packageResponseDtoList.add(packageResponseDto);
            }

            return packageResponseDtoList;
        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when fetching all packages and its images: {}", ex.getResponseCode());
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when fetching all packages and its images: {}", ex.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public PackageResponseDto findById(String packageId) throws ResourceNotFoundException {
        logger.info("entering find package by id service impl..");
        try {
            List<PackageImageResponseDto> images = new ArrayList<>();
            List<ItineraryResponseDto> itinerariesList = new ArrayList<>();
            Optional<Package> packageOptional = this.packageRepository.findById(packageId);

            if (packageOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Package is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            Set<String> imageIds = packageOptional.get().getImageIds();
            Set<String> itineraryIds = packageOptional.get().getItineraryIds();
            PackageResponseDto packageResponseDto = this.mapper.convertValue(packageOptional.get(), PackageResponseDto.class);

            for (String imageId : imageIds) {
                Optional<PackageImage> imageOptional = this.packageImageRepository.findById(imageId);
                PackageImageResponseDto packageImageResponseDto = this.mapper.convertValue(imageOptional.get(), PackageImageResponseDto.class);
                packageImageResponseDto.setImageData(this.getImageData(imageOptional.get().getImagePath()));
                images.add(packageImageResponseDto);
            }

            packageResponseDto.setPackageImageDetails(images);

            for (String itineraryId : itineraryIds) {
                Optional<PackageItinerary> itineraryOptional = this.packageItineraryRepository.findById(itineraryId);
                ItineraryResponseDto itineraryResponseDto = this.mapper.convertValue(itineraryOptional.get(), ItineraryResponseDto.class);
                itinerariesList.add(itineraryResponseDto);
            }

            packageResponseDto.setItineraryDetails(itinerariesList);

            return packageResponseDto;
        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when fetching a package by its id: {}", ex.getMessage());
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when fetching a package by its id: {}", ex.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void removePackageImageByImageName(String packageId, String imageName, AuditLog auditLog) throws ResourceDeleteException, ResourceNotFoundException  {
        logger.info("entering removePackageImageByImageName service impl...");
        auditLog.setAction(AuditLogAction.DELETE_PACKAGE_IMAGE);
        auditLog.setLoggedAt(LocalDateTime.now());
        try {

            Optional<PackageImage> packageImageOptional = this.packageImageRepository.findByImageName(imageName);

            if (packageImageOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Package image is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            this.packageImageRepository.delete(packageImageOptional.get());
            logger.info("image {} successfully removed from the db", imageName);
        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when updating a package: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when removing a package image: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new ResourceDeleteException(ResponseCode.INTERNAL_SERVER_ERROR);
        } finally {
            this.auditLogService.save(auditLog);
        }
    }

    @Override
    public byte[] downloadImage(String id) {
        try {
            Optional<PackageImage> fileData = this.packageImageRepository.findById(id);
            String filePath=fileData.get().getImagePath();
            byte[] images = Files.readAllBytes(new File(filePath).toPath());
            return images;
        }catch (Exception ex) {
            logger.info("");
        }
        return null;
    }


    private byte[] getImageData(String imagePath) throws Exception {
        try {
            return Files.readAllBytes(new File(imagePath).toPath());
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    private void saveItineraryDetails(List<CreateItineraryDto> itineraryDetails, String packageId, Set<String> packageItineraryIds) {
        for(CreateItineraryDto itineraryDto : itineraryDetails){
            PackageItinerary packageItinerary = PackageItinerary.builder()
                    .heading(itineraryDto.getHeading())
                    .startDate(itineraryDto.getStartDate())
                    .endDate(itineraryDto.getEndDate())
                    .description(itineraryDto.getDescription())
                    .packageId(packageId)
                    .build();

            PackageItinerary savePackageItinerary = this.packageItineraryRepository.save(packageItinerary);
            packageItineraryIds.add(savePackageItinerary.getId());
        }
    }

    private void savePackageImages(MultipartFile[] files, String packageId, Set<String> imageIdSet) throws Exception {
        try {
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String imagePath = this.imageFolderPath + originalFilename;
                String fileContentType = file.getContentType();
                String mimeType = fileContentType.split("/")[1];

                PackageImage image = PackageImage.builder()
                        .packageId(packageId)
                        .imageName(originalFilename)
                        .imageType(fileContentType)
                        .imagePath(imagePath)
                        .mimeType(mimeType)
                        .build();

                file.transferTo(new File(imagePath));

                String id = this.packageImageRepository.save(image).getId();
                imageIdSet.add(id);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }
}
