package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.document.TailorMadePackage;
import com.tuskerlanka.tourismmangement.dto.CreateTailorMadePackageDto;
import com.tuskerlanka.tourismmangement.dto.TailorMadePackageResponseDto;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.CreateTailorMadePackageException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import com.tuskerlanka.tourismmangement.repository.TailorMadePackageRepository;
import com.tuskerlanka.tourismmangement.service.TailorMadePackageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<TailorMadePackageResponseDto> findAll() throws ResourceNotFoundException{
        logger.info("findAll");
        try{
            List<TailorMadePackageResponseDto> tailorMadePackageResponseList = new ArrayList<>();
            List<TailorMadePackage> tailorMadePackages = this.tailorMadePackageRepository.findAll();


            if (tailorMadePackages.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("No tailor made packages available at the moment");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            for(TailorMadePackage tailorMadePackage : tailorMadePackages){
                TailorMadePackageResponseDto tailorMadePackageResponseDto = this.objectMapper.convertValue(tailorMadePackage, TailorMadePackageResponseDto.class);
                tailorMadePackageResponseList.add(tailorMadePackageResponseDto);
            }


            return tailorMadePackageResponseList;

        }catch (ResourceNotFoundException e){
            logger.error("unknown error occur when finding tailor made packages: {}", e.getResponseCode());
            throw new ResourceNotFoundException(e.getResponseCode());
        }catch (Exception e){
            logger.error("unknown error occur when fetching all tailor made packages: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public TailorMadePackageResponseDto findAllById(String tailorMadeId) throws ResourceNotFoundException{
        logger.info("findAllById");
        try{
            Optional<TailorMadePackage> tailorMadePackage = this.tailorMadePackageRepository.findById(tailorMadeId);

            if (tailorMadePackage.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Tailor made Package is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            return objectMapper.convertValue(tailorMadePackage.get(), TailorMadePackageResponseDto.class);

        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when fetching a tailor made package by its id: {}", ex.getMessage());
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception e){
            logger.error("unknown error occur when fetching a tailor made package by its id: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteTailorMadePackage(String tailorMadeId, AuditLog auditLog) throws ResourceDeleteException{
        logger.info("deleteTailorMadePackage");
        try{
            Optional<TailorMadePackage> TailorMadePackageOptional = this.tailorMadePackageRepository.findById(tailorMadeId);
            if (TailorMadePackageOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Tailor made Package is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            TailorMadePackage TailorMadePackage = TailorMadePackageOptional.get();

            this.tailorMadePackageRepository.delete(TailorMadePackage);
        }catch (ResourceDeleteException e){
            logger.error("error occur when deleting a package: {}", e.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
        }
        finally{
            this.auditLogService.save(auditLog);
        }
    }
}
