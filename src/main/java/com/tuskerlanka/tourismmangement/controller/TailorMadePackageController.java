package com.tuskerlanka.tourismmangement.controller;


import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreateTailorMadePackageDto;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import com.tuskerlanka.tourismmangement.service.TailorMadePackageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.tuskerlanka.tourismmangement.constants.ApplicationConstants.USER_SYSTEM;


@RestController
@RequestMapping(path = "/api/v1/tailor-made-package")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class TailorMadePackageController {

    private final static Logger logger = LoggerFactory.getLogger(TailorMadePackageController.class);

    private final TailorMadePackageService tailorMadePackageService;

    private final static String END_POINT = "/api/v1/tailor-made-package";

    @PostMapping(path = "/save")
    public ResponseEntity<ApiResponse> save(@RequestBody CreateTailorMadePackageDto createTailorMadePackageDto){
        logger.info("entering save destination api end point..");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("POST");
        auditLog.setEndPoint(END_POINT+"/save");
        auditLog.setUsername(USER_SYSTEM);

        this.tailorMadePackageService.saveTailorMadePackage(createTailorMadePackageDto, auditLog);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.getInstance(ResponseCode.TAILOR_MADE_PACKAGE_CREATED_SUCCESSFULLY));
    }

}
