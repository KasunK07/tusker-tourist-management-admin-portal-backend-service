package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreatePackageDto;
import com.tuskerlanka.tourismmangement.dto.PackageResponseDto;
import com.tuskerlanka.tourismmangement.dto.UpdatePackageDto;
import com.tuskerlanka.tourismmangement.exception.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PackageService {

    void save(CreatePackageDto createPackageDto, MultipartFile imageFile[], AuditLog auditLog) throws CreatePackageException;

    void updatePackage(String packageId, UpdatePackageDto updatePackageDto, MultipartFile file, AuditLog auditLog) throws ResourceNotFoundException, UpdatePackageException;

    List<PackageResponseDto> findAll() throws ResourceNotFoundException;

    PackageResponseDto findById(String packageId) throws ResourceNotFoundException;

    void removePackageImageByImageName(String packageId, String imageName, AuditLog auditLog) throws ResourceNotFoundException, ResourceDeleteException;

    void deletePackage(String packageId, AuditLog auditLog) throws BaseException;

    byte[] downloadImage(String id);
}
