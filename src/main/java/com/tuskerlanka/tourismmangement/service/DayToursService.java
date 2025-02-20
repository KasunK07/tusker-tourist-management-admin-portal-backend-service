package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreateDayToursDto;
import com.tuskerlanka.tourismmangement.dto.DayToursResponseDto;
import com.tuskerlanka.tourismmangement.exception.CreateDayTourException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DayToursService {

    void saveDayTours(CreateDayToursDto createDayToursDto, MultipartFile imageFile, AuditLog auditLog) throws CreateDayTourException;

    List<DayToursResponseDto> findAllDayTours() throws ResourceNotFoundException;

    DayToursResponseDto findDayTourById(String imageId) throws ResourceNotFoundException;
}
