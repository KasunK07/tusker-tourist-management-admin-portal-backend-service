package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.document.DayTours;
import com.tuskerlanka.tourismmangement.document.PackageImage;
import com.tuskerlanka.tourismmangement.document.TourImage;
import com.tuskerlanka.tourismmangement.dto.*;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.BaseException;
import com.tuskerlanka.tourismmangement.exception.CreateDayTourException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import com.tuskerlanka.tourismmangement.repository.DayToursImageRepository;
import com.tuskerlanka.tourismmangement.repository.DayToursRepository;
import com.tuskerlanka.tourismmangement.repository.PackageRepository;
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

    private final static Logger logger = LoggerFactory.getLogger(PackageRepository.class);

    private final DayToursRepository dayToursRepository;

    private final DayToursImageRepository imageRepository;

    private final AuditLogServiceImpl auditLogService;

    private final ObjectMapper objectMapper;

    @Value("${package.image.folder.path}")
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

            this.savePackageImages(imageFile, dayToursId, imageIdSet);

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

    private void savePackageImages(MultipartFile imageFile, String tourId, Set<String> imageIdSet) throws Exception {
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

//    public void updateDayTours(String DayToursId, UpdateDayTourDto updateDayTourDto, AuditLog auditLog) throws BaseException{
//        logger.info("Updating day tours into database");
//        auditLog.setAction(AuditLogAction.UPDATE_DAY_TOUR);
//        auditLog.setLoggedAt(LocalDateTime.now());
//
//        try{
//            logger.info("{}",updateDayTourDto);
//
//            Optional<DayTours> dayToursOptional = this.dayToursRepository.findById(DayToursId);
//
//            if(dayToursOptional.isEmpty()){
//                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("error in retrieving data..");
//                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
//            }
//
//            dayToursOptional.get().setName(updateDayTourDto.getName());
//            dayToursOptional.get().setDescription(updateDayTourDto.getDescription());
//            dayToursOptional.get().setImageIds(updateDayTourDto.getImageDetails());
//
//        }catch (Exception e){}
//
//    }

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


}
