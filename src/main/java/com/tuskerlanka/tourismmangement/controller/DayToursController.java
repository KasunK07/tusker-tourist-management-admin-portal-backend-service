package com.tuskerlanka.tourismmangement.controller;


import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreateDayToursDto;
import com.tuskerlanka.tourismmangement.dto.DayToursResponseDto;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.BaseException;
import com.tuskerlanka.tourismmangement.exception.CreateDayTourException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import com.tuskerlanka.tourismmangement.service.DayToursService;
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
@RequestMapping(path = "/api/v1/dayTours")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class DayToursController {

    private final static Logger logger = LoggerFactory.getLogger(DayToursController.class);

    private final DayToursService dayToursService;

    private final static String END_POINT = "/api/v1/dayTours";

    @PostMapping(path = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> save(@RequestPart(name = "tour") @Valid CreateDayToursDto createDayToursDto,
                                            @RequestPart(name = "images") @Valid @ImageFile MultipartFile imageFile) throws CreateDayTourException {
        logger.info("entering save package api end point..");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("POST");
        auditLog.setEndPoint(END_POINT+"/save");
        auditLog.setUsername(USER_SYSTEM);

        this.dayToursService.saveDayTours(createDayToursDto, imageFile, auditLog);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.getInstance(ResponseCode.DAY_TOUR_CREATED_SUCCESSFULLY));
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<ApiResponse> getAll() throws ResourceNotFoundException {
        logger.info("entering getAll day tours api end point..");

        List<DayToursResponseDto> response = this.dayToursService.findAllDayTours();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, response));

    }

    @GetMapping(path = "/read/{tourId}")
    public ResponseEntity<ApiResponse> readBy(@PathVariable String tourId) throws ResourceNotFoundException {
        logger.info("entering read day tours by its id end point...");

        DayToursResponseDto response = this.dayToursService.findDayTourById(tourId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, response));
    }

    @DeleteMapping(path = "/delete/{tourId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String tourId) throws BaseException {
        logger.info("entering delete day tours api end point...");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("DELETE");
        auditLog.setEndPoint(END_POINT+"/delete");
        auditLog.setUsername(USER_SYSTEM);

        dayToursService.deleteDayToursById(tourId,auditLog);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DAY_TOUR_DELETED_SUCCESSFULLY));
    }

    @GetMapping(path = "/get/count")
    public ResponseEntity<ApiResponse> getCount() throws BaseException {
        logger.info("entering get package api end point...");

        int count = dayToursService.getCount();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, count));
    }
}
