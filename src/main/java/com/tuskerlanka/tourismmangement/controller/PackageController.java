package com.tuskerlanka.tourismmangement.controller;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreatePackageDto;
import com.tuskerlanka.tourismmangement.dto.PackageResponseDto;
import com.tuskerlanka.tourismmangement.dto.UpdatePackageDto;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.*;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import com.tuskerlanka.tourismmangement.service.PackageService;
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

import java.io.File;
import java.util.List;

import static com.tuskerlanka.tourismmangement.constants.ApplicationConstants.USER_SYSTEM;

@RestController
@RequestMapping(path = "/api/v1/package")
@RequiredArgsConstructor
@Validated
// @Tag(name = "Business Controller API Endpoints.") remove the comment once swagger is added.
@CrossOrigin(origins = "*")
public class PackageController {

    private final static Logger logger = LoggerFactory.getLogger(PackageController.class);

    private final PackageService packageService;

    private final static String END_POINT = "/api/v1/package";

    @PostMapping(path = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> save(@RequestPart(name = "package") @Valid CreatePackageDto createPackageDto,
                                            @RequestPart(name = "images", required = false) MultipartFile[] imageFiles) throws CreatePackageException {
        logger.info("entering save package api end point..");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("POST");
        auditLog.setEndPoint(END_POINT+"/save");
        auditLog.setUsername(USER_SYSTEM);

        this.packageService.save(createPackageDto, imageFiles, auditLog);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.getInstance(ResponseCode.PACKAGE_CREATED_SUCCESSFULLY));
    }

    @GetMapping(path = "/image/read/{id}")
    public ResponseEntity<?> downloadImage(@PathVariable String id) {
        byte[] imageData = this.packageService.downloadImage(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

    @PutMapping(path = "/update/{packageId}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable String packageId,
            @RequestPart(name = "package") UpdatePackageDto updatePackageDto,
            @RequestPart(name = "images", required = false) @Valid MultipartFile[] imageFiles)
            throws ResourceNotFoundException, UpdatePackageException {
        logger.info("entering update package api end point...");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("PUT");
        auditLog.setEndPoint(END_POINT + "/update/" + packageId);
        auditLog.setUsername(USER_SYSTEM);

        this.packageService.updatePackage(packageId, updatePackageDto, imageFiles, auditLog);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.getInstance(ResponseCode.PACKAGE_UPDATED_SUCCESSFULLY));
    }

    @GetMapping(path = "/read-all")
    public ResponseEntity<ApiResponse> readAll() throws ResourceNotFoundException {
        logger.info("entering read all packages api end point...");

        List<PackageResponseDto> response = this.packageService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, response));
    }

    @GetMapping(path = "/read/{packageId}")
    public ResponseEntity<ApiResponse> readBy(@PathVariable String packageId) throws ResourceNotFoundException {
        logger.info("entering read package by its id end point...");

        PackageResponseDto response = this.packageService.findById(packageId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, response));
    }

    @DeleteMapping(path = "/delete/{packageId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String packageId) throws BaseException {
        logger.info("entering delete package api end point...");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("DELETE");
        auditLog.setEndPoint(END_POINT+"/delete");
        auditLog.setUsername(USER_SYSTEM);

        packageService.deletePackage(packageId,auditLog);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.PACKAGE_DELETED_SUCCESSFULLY));
    }

    @GetMapping(path = "/get/count")
    public ResponseEntity<ApiResponse> getCount() throws BaseException {
        logger.info("entering get package api end point...");

        int count = packageService.getCount();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, count));
    }

    @DeleteMapping(path = "/delete/image/{imageName}")
    public ResponseEntity<?> deleteImage(@PathVariable String imageName) throws ResourceDeleteException, ResourceNotFoundException {

        logger.info("Getting the image by its name end point...{}", imageName);
        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("DELETE");
        auditLog.setEndPoint(END_POINT+"/delete/image/" + imageName);
        auditLog.setUsername(USER_SYSTEM);

        packageService.removePackageImageByImageName(imageName,auditLog);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
