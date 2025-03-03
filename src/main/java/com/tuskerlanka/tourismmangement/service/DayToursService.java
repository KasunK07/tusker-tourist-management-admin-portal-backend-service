package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreateDayToursDto;
import com.tuskerlanka.tourismmangement.dto.DayToursResponseDto;
import com.tuskerlanka.tourismmangement.dto.UpdateDayTourDto;
import com.tuskerlanka.tourismmangement.exception.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DayToursService {

    void saveDayTours(CreateDayToursDto createDayToursDto, MultipartFile imageFile, AuditLog auditLog) throws CreateDayTourException;

    @Transactional(propagation = Propagation.REQUIRED)
    void updateDayTours(String dayTourId, UpdateDayTourDto updateDayToursDto, MultipartFile file, AuditLog auditLog)
            throws ResourceNotFoundException, UpdatePackageException;

    List<DayToursResponseDto> findAllDayTours() throws ResourceNotFoundException;

    DayToursResponseDto findDayTourById(String imageId) throws ResourceNotFoundException;

    void deleteDayToursById(String tourId, AuditLog auditLog) throws BaseException;

    byte[] downloadImage(String id);

    @Transactional(propagation = Propagation.REQUIRED)
    void removePackageImageByImageName(String imageName, AuditLog auditLog) throws ResourceDeleteException, ResourceNotFoundException;

    int getCount();
}
