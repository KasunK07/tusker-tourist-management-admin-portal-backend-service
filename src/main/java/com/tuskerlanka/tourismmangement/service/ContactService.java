package com.tuskerlanka.tourismmangement.service;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.ContactResponseDto;
import com.tuskerlanka.tourismmangement.dto.CreateContactDto;
import com.tuskerlanka.tourismmangement.exception.CreateContactException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContactService {
    @Transactional(propagation = Propagation.REQUIRED)
    void saveContactDetails(CreateContactDto createContactDto, AuditLog auditLog) throws CreateContactException;

    List<ContactResponseDto> findAll() throws ResourceNotFoundException;

    ContactResponseDto findAllById(String contactId) throws ResourceNotFoundException;

    void deleteContactDetails(String contactId, AuditLog auditLog) throws ResourceDeleteException;
}
