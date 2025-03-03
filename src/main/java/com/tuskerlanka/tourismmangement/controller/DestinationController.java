package com.tuskerlanka.tourismmangement.controller;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreateDestinationDto;
import com.tuskerlanka.tourismmangement.dto.DestinationResponseDto;
import com.tuskerlanka.tourismmangement.dto.UpdateDayTourDto;
import com.tuskerlanka.tourismmangement.dto.UpdateDestinationsDto;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.*;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import com.tuskerlanka.tourismmangement.service.DestinationService;
import com.tuskerlanka.tourismmangement.validation.image.ImageFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.tuskerlanka.tourismmangement.constants.ApplicationConstants.USER_SYSTEM;

@RestController
@RequestMapping(path = "/api/v1/destinations")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class DestinationController {
    private final static Logger logger = LoggerFactory.getLogger(DestinationController.class);

    private final DestinationService destinationService;

    private final static String END_POINT = "/api/v1/destinations";

    @PostMapping(path = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> save(@RequestPart(name = "destination") @Valid CreateDestinationDto createDestinationDto,
                                            @RequestPart(name = "images") @Valid @ImageFile MultipartFile imageFile) throws CreateDestinationException {
        logger.info("entering save destination api end point..");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("POST");
        auditLog.setEndPoint(END_POINT+"/save");
        auditLog.setUsername(USER_SYSTEM);

        this.destinationService.saveDestinations(createDestinationDto, imageFile, auditLog);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.getInstance(ResponseCode.DESTINATION_CREATED_SUCCESSFULLY));
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<ApiResponse> getAll() throws ResourceNotFoundException {
        logger.info("entering getAll destinations api end point..");

        List<DestinationResponseDto> response = this.destinationService.findAllDestinations();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, response));

    }

    @GetMapping(path = "/read/{destinationId}")
    public ResponseEntity<ApiResponse> readBy(@PathVariable String destinationId) throws ResourceNotFoundException {
        logger.info("entering read destination by its id end point...");

        DestinationResponseDto response = this.destinationService.findDestinationById(destinationId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, response));
    }

    @PutMapping(path = "/update/{destinationId}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable String destinationId,
            @RequestPart(name = "destination") UpdateDestinationsDto destinationsDto,
            @RequestPart(name = "images", required = false) @Valid MultipartFile imageFiles)
            throws ResourceNotFoundException, UpdateDestinationException {
        logger.info("entering update day tour api end point...");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("PUT");
        auditLog.setEndPoint(END_POINT + "/update/" + destinationId);
        auditLog.setUsername(USER_SYSTEM);

        this.destinationService.updateDestinations(destinationId, destinationsDto, imageFiles, auditLog);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.getInstance(ResponseCode.DESTINATION_UPDATED_SUCCESSFULLY));
    }

    @DeleteMapping(path = "/delete/{destinationId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String destinationId) throws BaseException {
        logger.info("entering delete destination api end point...");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("DELETE");
        auditLog.setEndPoint(END_POINT+"/delete");
        auditLog.setUsername(USER_SYSTEM);

        destinationService.deleteDestinationsById(destinationId,auditLog);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DESTINATION_DELETED_SUCCESSFULLY));
    }

    @GetMapping(path = "/get/count")
    public ResponseEntity<ApiResponse> getCount() throws BaseException {
        logger.info("entering get package api end point...");

        int count = destinationService.getCount();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, count));
    }

    @DeleteMapping(path = "/delete/image/{imageName}")
    public ResponseEntity<?> deleteImage(@PathVariable String imageName) throws ResourceDeleteException, ResourceNotFoundException {

        logger.info("Getting the image by its name end point...{}", imageName);
        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("DELETE");
        auditLog.setEndPoint(END_POINT+"/delete/image/" + imageName);
        auditLog.setUsername(USER_SYSTEM);

        destinationService.removePackageImageByImageName(imageName,auditLog);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
