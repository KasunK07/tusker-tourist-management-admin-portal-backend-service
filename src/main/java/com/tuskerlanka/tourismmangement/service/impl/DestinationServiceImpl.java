package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.*;
import com.tuskerlanka.tourismmangement.dto.*;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.CreateDestinationException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import com.tuskerlanka.tourismmangement.exception.UpdatePackageException;
import com.tuskerlanka.tourismmangement.repository.*;
import com.tuskerlanka.tourismmangement.service.DestinationService;
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
public class DestinationServiceImpl implements DestinationService {

    private final static Logger logger = LoggerFactory.getLogger(DestinationRepository.class);

    private final DestinationRepository destinationRepository;

    private final DestinationImageRepository imageRepository;

    private final AuditLogServiceImpl auditLogService;

    private final ObjectMapper objectMapper;

    @Value("${destinations.image.folder.path}")
    private String imageFolderPath;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveDestinations(CreateDestinationDto createDestinationDto, MultipartFile imageFile, AuditLog auditLog) throws CreateDestinationException {
        logger.info("Saving destinations into database");
        auditLog.setAction(AuditLogAction.ADD_NEW_DESTINATION);
        auditLog.setLoggedAt(LocalDateTime.now());

        try{
            logger.info("{}",createDestinationDto);
            Set<String> imageIdSet = new HashSet<>();

            Destinations destinations = this.objectMapper.convertValue(createDestinationDto,Destinations.class);
            destinations.setCreatedAt(LocalDateTime.now());

            Destinations savedDestinations = this.destinationRepository.save(destinations);
            logger.info("new destinations successfully added to the db");

            final String destinationId = savedDestinations.getId();

            this.saveDestinationImages(imageFile, destinationId, imageIdSet);


            savedDestinations.setImageIds(imageIdSet);
            this.destinationRepository.save(savedDestinations);
            logger.info("destinations image ids are successfully saved with destinations");

        }catch (Exception e){
            logger.error("unknown error occur when creating a new destinations: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new CreateDestinationException(ResponseCode.INTERNAL_SERVER_ERROR);
        }finally {
            this.auditLogService.save(auditLog);
        }
    }

    private void saveDestinationImages(MultipartFile imageFile, String descriptionId, Set<String> imageIdSet) throws Exception {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String imagePath = this.imageFolderPath + originalFilename;
            String fileContentType = imageFile.getContentType();
            String mimeType = fileContentType.split("/")[1];

            DestinationImage destinationImage = DestinationImage.builder()
                    .destinationId(descriptionId)
                    .imagePath(imagePath)
                    .imageName(originalFilename)
                    .imageType(fileContentType)
                    .mimeType(mimeType)
                    .build();
            imageFile.transferTo(new File(imagePath));
            String id = this.imageRepository.save(destinationImage).getId();
            imageIdSet.add(id);

        } catch (Exception ex) {
            throw new Exception("Error saving image", ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDestinations(String destinationId, UpdateDestinationsDto updateDestinationsDto, MultipartFile file, AuditLog auditLog)
            throws ResourceNotFoundException, UpdatePackageException {
        logger.info("entering update destinations service impl...");
        auditLog.setAction(AuditLogAction.UPDATE_DESTINATION);
        auditLog.setLoggedAt(LocalDateTime.now());

        try {
            Optional<Destinations> destinationsOptional = this.destinationRepository.findById(destinationId);

            if (destinationsOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Destination is not available");
                auditLog.setStatus(AuditLogStatus.FAILED);
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            Destinations existingDestinations = destinationsOptional.get();
            existingDestinations.setName(updateDestinationsDto.getName());
            existingDestinations.setDescription(updateDestinationsDto.getDescription());

            if (file != null) {
                Set<String> newImageIds = new HashSet<>();
                saveDestinationImages(file, destinationId, newImageIds);
                Set<String> currentImageIds = existingDestinations.getImageIds() != null ? existingDestinations.getImageIds() : new HashSet<>();
                currentImageIds.addAll(newImageIds);
                existingDestinations.setImageIds(currentImageIds);
            }

            destinationRepository.save(existingDestinations);
            auditLog.setStatus(AuditLogStatus.SUCCESS);
            logger.info("Destinations {} successfully updated", destinationId);

        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when updating a destination: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("unknown error occur when updating a destination: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new UpdatePackageException(ResponseCode.INTERNAL_SERVER_ERROR);
        } finally {
            this.auditLogService.save(auditLog);
        }
    }


    @Override
    public List<DestinationResponseDto> findAllDestinations() throws ResourceNotFoundException {
        logger.info("Finding all day tours in database");
        try{
            List<DestinationResponseDto> destinationResponseDtoList = new ArrayList<>();
            List<Destinations> destinationList = this.destinationRepository.findAll();

            for(Destinations destinations : destinationList){
                List<DestinationImageResponseDto> destinationResponseList = new ArrayList<>();
                DestinationResponseDto destinationResponseDto = new DestinationResponseDto();
                destinationResponseDto.setId(destinations.getId());
                destinationResponseDto.setName(destinations.getName());
                destinationResponseDto.setDescription(destinations.getDescription());
                Set<String> imageIds = destinations.getImageIds();

                for (String id: imageIds) {
                    Optional<DestinationImage> imageById = this.imageRepository.findById(id);
                    DestinationImageResponseDto destinationImageResponseDto = new DestinationImageResponseDto();
                    destinationImageResponseDto.setImageName(imageById.get().getImageName());
                    destinationImageResponseDto.setImagePath(imageById.get().getImageType());
                    destinationImageResponseDto.setMimeType(imageById.get().getMimeType());
                    destinationImageResponseDto.setImageData(this.getImageData(imageById.get().getImagePath()));
                    destinationResponseList.add(destinationImageResponseDto);
                }

                destinationResponseDto.setImageDetails(destinationResponseList);
                destinationResponseDtoList.add(destinationResponseDto);
            }

            return destinationResponseDtoList;
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
    public DestinationResponseDto findDestinationById(String imageId) throws ResourceNotFoundException{
        logger.info("Finding destination by id in database");
        try {
            List<DestinationImageResponseDto> images = new ArrayList<>();
            Optional<Destinations> destinationsOptional = this.destinationRepository.findById(imageId);

            if (destinationsOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Destinations is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            Set<String> imageIds = destinationsOptional.get().getImageIds();
            DestinationResponseDto destinationsResponseDto = this.objectMapper.convertValue(destinationsOptional.get(), DestinationResponseDto.class);

            for (String destinationImageId : imageIds) {
                Optional<DestinationImage> imageOptional = this.imageRepository.findById(destinationImageId);
                DestinationImageResponseDto destinationImageResponseDto = this.objectMapper.convertValue(imageOptional.get(), DestinationImageResponseDto.class);
                destinationImageResponseDto.setImageData(this.getImageData(imageOptional.get().getImagePath()));
                images.add(destinationImageResponseDto);
            }

            destinationsResponseDto.setImageDetails(images);

            return destinationsResponseDto;
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
    public void deleteDestinationsById(String destinationId, AuditLog auditlog) throws ResourceDeleteException {
        logger.info("Deleting day tour by id in database");
        auditlog.setLoggedAt(LocalDateTime.now());
        try{
            Optional<Destinations> destinationsOptional = this.destinationRepository.findById(destinationId);
            if (destinationsOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Day tour is not available");
                throw new ResourceDeleteException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            Destinations destinationsToDelete = destinationsOptional.get();

            Set<String> imageIds = destinationsToDelete.getImageIds();

            for (String imageId : imageIds) {
                Optional<DestinationImage> imageOptional = this.imageRepository.findById(imageId);

                if(imageOptional.isPresent()) {
                    File imageFile = new File(imageOptional.get().getImagePath());
                    if(imageFile.exists()) {
                        logger.info("Failed to Delete the image Path {}", imageId);
                    }
                }
                this.imageRepository.delete(imageOptional.get());
            }
            this.destinationRepository.delete(destinationsToDelete);
            logger.info("Package {} successfully deleted", destinationId);
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
            Optional<DestinationImage> fileData = this.imageRepository.findById(id);
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
        auditLog.setAction(AuditLogAction.DELETE_DESTINATION_IMAGE);
        auditLog.setLoggedAt(LocalDateTime.now());

        try {
            Optional<DestinationImage> dayDestinationOptional = this.imageRepository.findByImageName(imageName);

            if (dayDestinationOptional.isEmpty()) {
                logger.warn("Destination image with name {} not found", imageName);
                ResponseCode.DATA_RETRIEVED_ERROR.setMessage("Destination image is not available");
                auditLog.setStatus(AuditLogStatus.FAILED);
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            DestinationImage destinationImage = dayDestinationOptional.get();
            String imageId = destinationImage.getId();
            String imagePath = destinationImage.getImagePath();

            List<Destinations> destinationsUsingImage = destinationRepository.findByImageIdsContaining(imageId);
            boolean isUsedByOtherDestinations = destinationsUsingImage.size() > 1;

            imageRepository.delete(destinationImage);
            logger.info("Image {} removed from DestinationImage collection", imageName);
            for (Destinations destinations : destinationsUsingImage) {
                Set<String> imageIds = destinations.getImageIds();
                if (imageIds.remove(imageId)) {
                    destinationRepository.save(destinations);
                    logger.info("Removed imageId {} from destinations {}", imageId, destinations.getId());
                }
            }
            if (!isUsedByOtherDestinations) {
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
                logger.info("Image {} is used by other destinations, keeping it in filesystem", imageName);
            }

            logger.info("Image {} successfully removed from the db and related destinations", imageName);
            auditLog.setStatus(AuditLogStatus.SUCCESS);

        } catch (ResourceNotFoundException ex) {
            logger.error("Error occurred when removing destinations image: {}", ex.getResponseCode());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception ex) {
            logger.error("Unknown error occurred when removing a destinations image: {}", ex.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(ex.getMessage());
            throw new ResourceDeleteException(ResponseCode.INTERNAL_SERVER_ERROR);
        } finally {
            this.auditLogService.save(auditLog);
        }
    }

    @Override
    public int getCount(){
        return (int) destinationRepository.count();
    }
}
