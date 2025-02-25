package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.document.TailorMadePackage;
import com.tuskerlanka.tourismmangement.dto.CreateTailorMadePackageDto;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.CreateTailorMadePackageException;
import com.tuskerlanka.tourismmangement.repository.TailorMadePackageRepository;
import com.tuskerlanka.tourismmangement.service.TailorMadePackageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TailorMadePackageServiceImpl implements TailorMadePackageService {

    private final static Logger logger = LoggerFactory.getLogger(TailorMadePackageRepository.class);

    private final TailorMadePackageRepository tailorMadePackageRepository;

    private final ObjectMapper objectMapper;

    private final AuditLogServiceImpl auditLogService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveTailorMadePackage(CreateTailorMadePackageDto createTailorMadePackageDto, AuditLog auditLog) throws CreateTailorMadePackageException {
        logger.info("saveTailorMadePackage");
        auditLog.setLoggedAt(LocalDateTime.now());
        auditLog.setAction(AuditLogAction.ADD_NEW_TAILOR_MADE_PACKAGE);
        try{

            TailorMadePackage tailorMadePackage = objectMapper.convertValue(createTailorMadePackageDto, TailorMadePackage.class);

            TailorMadePackage savedTailorMadePackage = tailorMadePackageRepository.save(tailorMadePackage);

            this.tailorMadePackageRepository.save(savedTailorMadePackage);
            logger.info("saveTailorMadePackage {}", savedTailorMadePackage);
            auditLog.setStatus(AuditLogStatus.SUCCESS);

        }catch (Exception e){
            logger.error("unknown error occur when creating a new tailor made package: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new CreateTailorMadePackageException(ResponseCode.INTERNAL_SERVER_ERROR);
        }finally{
            auditLogService.save(auditLog);
        }
    }


}
