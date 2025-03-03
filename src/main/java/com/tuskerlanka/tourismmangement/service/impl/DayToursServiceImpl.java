package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.*;
import com.tuskerlanka.tourismmangement.dto.*;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.CreateDayTourException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import com.tuskerlanka.tourismmangement.exception.UpdatePackageException;
import com.tuskerlanka.tourismmangement.repository.DayToursImageRepository;
import com.tuskerlanka.tourismmangement.repository.DayToursRepository;
import com.tuskerlanka.tourismmangement.service.DayToursService;
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
public class DayToursServiceImpl implements DayToursService {

    private final static Logger logger = LoggerFactory.getLogger(DayToursRepository.class);

    private final DayToursRepository dayToursRepository;

    private final DayToursImageRepository imageRepository;

    private final AuditLogServiceImpl auditLogService;

    private final ObjectMapper objectMapper;

    @Value("${dayTours.image.folder.path}")
    private String imageFolderPath;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveDayTours(CreateDayToursDto createDayToursDto, MultipartFile imageFile, AuditLog auditLog) throws CreateDayTourException {
        logger.info("Saving day tours into database");
        auditLog.setAction(AuditLogAction.ADD_NEW_DAY_TOUR);
        auditLog.setLoggedAt(LocalDateTime.now());

        try{
            logger.info("{}",createDayToursDto);
            Set<String> imageIdSet = new HashSet<>();

            DayTours newTours = this.objectMapper.convertValue(createDayToursDto,DayTours.class);
            newTours.setCreatedAt(LocalDateTime.now());

            DayTours savedDayTours = this.dayToursRepository.save(newTours);
            logger.info("new package successfully added to the db");

            final String dayToursId = savedDayTours.getId();

            this.saveDayToursImages(imageFile, dayToursId, imageIdSet);

            savedDayTours.setImageIds(imageIdSet);
            this.dayToursRepository.save(savedDayTours);
            logger.info("package image ids are successfully saved with package");

        }catch (Exception e){
            logger.error("unknown error occur when creating a new day tour: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new CreateDayTourException(ResponseCode.INTERNAL_SERVER_ERROR);
        }finally {
            this.auditLogService.save(auditLog);
        }
    }

    private void saveDayToursImages(MultipartFile imageFile, String tourId, Set<String> imageIdSet) throws Exception {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String imagePath = this.imageFolderPath + originalFilename;
            String fileContentType = imageFile.getContentType();
            String mimeType = fileContentType.split("/")[1];

            TourImage dayToursImage = TourImage.builder()
                    .dayToursId(tourId)
                    .imagePath(imagePath)
                    .imageName(originalFilename)
                    .imageType(fileContentType)
                    .mimeType(mimeType)
                    .build();
            imageFile.transferTo(new File(imagePath));
            String id = this.imageRepository.save(dayToursImage).getId();
            imageIdSet.add(id);

        } catch (Exception ex) {
            throw new Exception("Error saving image", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDayTours(String dayTourId, UpdateDayTourDto updateDayToursDto, MultipartFile file, AuditLog auditLog)
            throws ResourceNotFoundException, UpdatePackageException {
        logger.info("entering update package service impl...");
        auditLog.setAction(AuditLogAction.UPDATE_DAY_TOUR);
        auditLog.setLoggedAt(LocalDateTime.now());

        try {
            Optional<DayTours> dayToursOptional = this.dayToursRepository.findById(dayTourId);

            if (dayToursOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Day Tour is not available");
                auditLog.setStatus(AuditLogStatus.FAILED);
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            DayTours existingDayTours = dayToursOptional.get();
            existingDayTours.setName(updateDayToursDto.getName());
            existingDayTours.setDescription(updateDayToursDto.getDescription());

            if (file != null) {
                Set<String> newImageIds = new HashSet<>();
                saveDayToursImages(file, dayTourId, newImageIds);
                Set<String> currentImageIds = existingDayTours.getImageIds() != null ? existingDayTours.getImageIds() : new HashSet<>();
                currentImageIds.addAll(newImageIds);
                existingDayTours.setImageIds(currentImageIds);
            }

            dayToursRepository.save(existingDayTours);
            auditLog.setStatus(AuditLogStatus.SUCCESS);
            logger.info("Day Tours {} successfully updated", dayTourId);

        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when updating a day tour: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when updating a day tour: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new UpdatePackageException(ResponseCode.INTERNAL_SERVER_ERROR);
        } finally {
            this.auditLogService.save(auditLog);
        }
    }

    @Override
    public List<DayToursResponseDto> findAllDayTours() throws ResourceNotFoundException{
        logger.info("Finding all day tours in database");
        try{
            List<DayToursResponseDto> dayToursResponseDtoList = new ArrayList<>();
            List<DayTours> daysToursList = this.dayToursRepository.findAll();

            for(DayTours dayTour : daysToursList){
                List<DayToursImageResponseDto> dayToursResponseList = new ArrayList<>();
                DayToursResponseDto dayToursResponseDto = new DayToursResponseDto();
                dayToursResponseDto.setId(dayTour.getId());
                dayToursResponseDto.setName(dayTour.getName());
                dayToursResponseDto.setDescription(dayTour.getDescription());
                Set<String> imageIds = dayTour.getImageIds();

                for (String id: imageIds) {
                    Optional<TourImage> imageById = this.imageRepository.findById(id);
                    DayToursImageResponseDto dayToursImageResponseDto = new DayToursImageResponseDto();
                    dayToursImageResponseDto.setImageName(imageById.get().getImageName());
                    dayToursImageResponseDto.setImagePath(imageById.get().getImageType());
                    dayToursImageResponseDto.setMimeType(imageById.get().getMimeType());
                    dayToursImageResponseDto.setImageData(this.getImageData(imageById.get().getImagePath()));
                    dayToursResponseList.add(dayToursImageResponseDto);
                }

                dayToursResponseDto.setImageDetails(dayToursResponseList);
                dayToursResponseDtoList.add(dayToursResponseDto);
            }

            return dayToursResponseDtoList;
        }catch (ResourceNotFoundException e){
            logger.error("error occur when fetching all day tours and its images: {}", e.getResponseCode());
            throw new ResourceNotFoundException(e.getResponseCode());
        }catch (Exception e){
            logger.error("unknown error occur when fetching all day tours and its images: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DayToursResponseDto findDayTourById(String imageId) throws ResourceNotFoundException{
        logger.info("Finding day tour by id in database");
        try {
            List<DayToursImageResponseDto> images = new ArrayList<>();
            Optional<DayTours> dayToursOptional = this.dayToursRepository.findById(imageId);

            if (dayToursOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Day tour is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            Set<String> imageIds = dayToursOptional.get().getImageIds();
            DayToursResponseDto dayToursResponseDto = this.objectMapper.convertValue(dayToursOptional.get(), DayToursResponseDto.class);

            for (String tourImageId : imageIds) {
                Optional<TourImage> imageOptional = this.imageRepository.findById(tourImageId);
                DayToursImageResponseDto dayToursImageResponseDto = this.objectMapper.convertValue(imageOptional.get(), DayToursImageResponseDto.class);
                dayToursImageResponseDto.setImageData(this.getImageData(imageOptional.get().getImagePath()));
                images.add(dayToursImageResponseDto);
            }

            dayToursResponseDto.setImageDetails(images);

            return dayToursResponseDto;
        }catch (ResourceNotFoundException e){
            logger.error("error occur when fetching a day tour by its id: {}", e.getMessage());
            throw new ResourceNotFoundException(e.getResponseCode());
        }catch (Exception e){
            logger.error("unknown error occur when fetching a day tour by its id: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteDayToursById(String tourId, AuditLog auditlog) throws ResourceDeleteException {
        logger.info("Deleting day tour by id in database");
        auditlog.setLoggedAt(LocalDateTime.now());
        try{
            Optional<DayTours> dayToursOptional = this.dayToursRepository.findById(tourId);
            if (dayToursOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Day tour is not available");
                throw new ResourceDeleteException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            DayTours dayToursToDelete = dayToursOptional.get();

            Set<String> imageIds = dayToursToDelete.getImageIds();

            for (String imageId : imageIds) {
                Optional<TourImage> imageOptional = this.imageRepository.findById(imageId);

                if(imageOptional.isPresent()) {
                    File imageFile = new File(imageOptional.get().getImagePath());
                    if(imageFile.exists()) {
                        logger.info("Failed to Delete the image Path {}", imageId);
                    }
                }
                this.imageRepository.delete(imageOptional.get());
            }
            this.dayToursRepository.delete(dayToursToDelete);
            logger.info("Package {} successfully deleted", tourId);
            auditlog.setStatus(AuditLogStatus.SUCCESS);
        }catch(Exception e){
            logger.error("error occur when deleting a day tours: {}", e.getMessage());
            auditlog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
        }finally {
            auditLogService.save(auditlog);
        }
    }

    @Override
    public byte[] downloadImage(String id) {
        try {
            Optional<TourImage> fileData = this.imageRepository.findById(id);
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

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void removePackageImageByImageName(String imageName, AuditLog auditLog) throws ResourceDeleteException, ResourceNotFoundException {
        logger.info("Entering removeDayToursImageByImageName service impl...");
        auditLog.setAction(AuditLogAction.DELETE_DAY_TOUR_IMAGE);
        auditLog.setLoggedAt(LocalDateTime.now());

        try {
            Optional<TourImage> dayToursImageOptional = this.imageRepository.findByImageName(imageName);

            if (dayToursImageOptional.isEmpty()) {
                logger.warn("Day Tours image with name {} not found", imageName);
                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Day Tours  image is not available");
                auditLog.setStatus(AuditLogStatus.FAILED);
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            TourImage tourImage = dayToursImageOptional.get();
            String imageId = tourImage.getId();
            String imagePath = tourImage.getImagePath();

            List<DayTours> dayToursUsingImage = dayToursRepository.findByImageIdsContaining(imageId);
            boolean isUsedByOtherDayTours = dayToursUsingImage.size() > 1;

            imageRepository.delete(tourImage);
            logger.info("Image {} removed from TourImage collection", imageName);
            for (DayTours tours : dayToursUsingImage) {
                Set<String> imageIds = tours.getImageIds();
                if (imageIds.remove(imageId)) {
                    dayToursRepository.save(tours);
                    logger.info("Removed imageId {} from day tours {}", imageId, tours.getId());
                }
            }
            if (!isUsedByOtherDayTours) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    if (imageFile.delete()) {
                        logger.info("Deleted image file: {}", imagePath);
                    } else {
                        logger.warn("Failed to delete image file: {}", imagePath);
                        ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Image Path Doesn't exists");
                        auditLog.setStatus(AuditLogStatus.FAILED);
                        throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
                    }
                } else {
                    logger.warn("Image file does not exist at path: {}", imagePath);
                }
            } else {
                logger.info("Image {} is used by other day tours, keeping it in filesystem", imageName);
            }

            logger.info("Image {} successfully removed from the db and related packages", imageName);
            auditLog.setStatus(AuditLogStatus.SUCCESS);

        } catch (ResourceNotFoundException ex) {
            logger.error("Error occurred when removing day tours image: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("Unknown error occurred when removing a day tours image: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new ResourceDeleteException(ResponseCode.INTERNAL_SERVER_ERROR);
        } finally {
            this.auditLogService.save(auditLog);
        }
    }

    @Override
    public int getCount(){
        return (int) dayToursRepository.count();
    }

}
