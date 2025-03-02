package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.CreateDestinationDto;
import com.tuskerlanka.tourismmangement.dto.DestinationResponseDto;
import com.tuskerlanka.tourismmangement.exception.CreateDestinationException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DestinationService {
    @Transactional(propagation = Propagation.REQUIRED)
    void saveDestinations(CreateDestinationDto createDestinationDto, MultipartFile imageFile, AuditLog auditLog) throws CreateDestinationException;

    List<DestinationResponseDto> findAllDestinations() throws ResourceNotFoundException;

    DestinationResponseDto findDestinationById(String imageId) throws ResourceNotFoundException;

    void deleteDestinationsById(String destinationId, AuditLog auditlog) throws ResourceDeleteException;

    byte[] downloadImage(String id);

    int getCount();
}
