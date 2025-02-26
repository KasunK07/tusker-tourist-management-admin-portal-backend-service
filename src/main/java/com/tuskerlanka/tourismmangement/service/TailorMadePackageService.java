package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreateTailorMadePackageDto;
import com.tuskerlanka.tourismmangement.dto.TailorMadePackageResponseDto;
import com.tuskerlanka.tourismmangement.exception.CreateDestinationException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

public interface TailorMadePackageService {
    @Transactional(propagation = Propagation.REQUIRED)
    void saveTailorMadePackage(CreateTailorMadePackageDto createTailorMadePackageDto, AuditLog auditLog) throws CreateDestinationException;

    List<TailorMadePackageResponseDto> findAll() throws ResourceNotFoundException;

    TailorMadePackageResponseDto findAllById(String tailorMadeId) throws ResourceNotFoundException;

    void deleteTailorMadePackage(String tailorMadeId, AuditLog auditLog) throws ResourceDeleteException;
}
