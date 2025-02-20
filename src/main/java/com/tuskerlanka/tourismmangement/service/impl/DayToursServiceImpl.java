package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.document.DayTours;
import com.tuskerlanka.tourismmangement.document.TourImage;
import com.tuskerlanka.tourismmangement.dto.CreateDayToursDto;
import com.tuskerlanka.tourismmangement.dto.DayToursImageResponseDto;
import com.tuskerlanka.tourismmangement.dto.DayToursResponseDto;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.CreateDayTourException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            String getFileName = imageFile.getOriginalFilename();
            String imagePath = imageFolderPath + getFileName;

            DayTours dayTours = this.objectMapper.convertValue(createDayToursDto,DayTours.class);
            dayTours.setCreatedAt(LocalDateTime.now());

            DayTours savedDayTours = this.dayToursRepository.save(dayTours);

            final String tourId = savedDayTours.getId();

            TourImage dayToursImage = TourImage.builder()
                    .dayToursId(tourId)
                    .imagePath(imagePath)
                    .imageName(imageFile.getOriginalFilename())
                    .imageType(imageFile.getContentType())
                    .build();

            TourImage tourImage = this.imageRepository.save(dayToursImage);

            savedDayTours.setImageIds(Set.of(tourImage.getId()));
            this.dayToursRepository.save(savedDayTours);

            imageFile.transferTo(new File(imagePath));
            logger.info("new day tour images added to the file system");

            auditLog.setStatus(AuditLogStatus.SUCCESS);

        }catch (Exception e){
            logger.error("unknown error occur when creating a new day tour: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new CreateDayTourException(ResponseCode.INTERNAL_SERVER_ERROR);
        }finally {
            this.auditLogService.save(auditLog);
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
                DayToursResponseDto dayToursResponseDto = new DayToursResponseDto();
                dayToursResponseDto.setName(dayTour.getName());
                dayToursResponseDto.setDescription(dayTour.getDescription());
                Set<String> imageIds = dayTour.getImageIds();

                for(String imageId : imageIds){
                    Optional<TourImage> tourImageOptional = this.imageRepository.findById(imageId);
                    DayToursImageResponseDto dayToursImageResponseDto = new DayToursImageResponseDto();
                    dayToursImageResponseDto.setImageName(tourImageOptional.get().getImageName());
                    dayToursImageResponseDto.setImagePath(tourImageOptional.get().getImagePath());
                    dayToursImageResponseDto.setImageData(this.getImageData(tourImageOptional.get().getImagePath()));
                    dayToursResponseDto.setImageDetails(dayToursImageResponseDto);
                }
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
            Optional<DayTours> dayToursOptional = this.dayToursRepository.findById(imageId);

            if (dayToursOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Day tour is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            Set<String> imageIds = dayToursOptional.get().getImageIds();
            DayToursResponseDto dayToursResponseDto = this.objectMapper.convertValue(dayToursOptional.get(), DayToursResponseDto.class);

            for(String imageids : imageIds){
                Optional<TourImage> tourImageOptional = this.imageRepository.findById(imageids);
                DayToursImageResponseDto dayToursImageResponseDto = this.objectMapper.convertValue(tourImageOptional.get(), DayToursImageResponseDto.class);
                dayToursImageResponseDto.setImageData(this.getImageData(tourImageOptional.get().getImagePath()));
                dayToursResponseDto.setImageDetails(dayToursImageResponseDto);
            }

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

    private byte[] getImageData(String imagePath) throws Exception {
        try {
            return Files.readAllBytes(new File(imagePath).toPath());
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }
}
