package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreateTailorMadePackageDto;
import com.tuskerlanka.tourismmangement.exception.CreateDestinationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface TailorMadePackageService {
    @Transactional(propagation = Propagation.REQUIRED)
    void saveTailorMadePackage(CreateTailorMadePackageDto createTailorMadePackageDto, AuditLog auditLog) throws CreateDestinationException;
}
