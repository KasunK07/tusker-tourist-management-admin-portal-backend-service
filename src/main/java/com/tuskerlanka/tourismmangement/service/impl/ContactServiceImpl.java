package com.tuskerlanka.tourismmangement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.document.Contact;
import com.tuskerlanka.tourismmangement.dto.ContactResponseDto;
import com.tuskerlanka.tourismmangement.dto.CreateContactDto;
import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.exception.CreateContactException;
import com.tuskerlanka.tourismmangement.exception.ResourceDeleteException;
import com.tuskerlanka.tourismmangement.exception.ResourceNotFoundException;
import com.tuskerlanka.tourismmangement.repository.ContactRepository;
import com.tuskerlanka.tourismmangement.service.ContactService;
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
public class ContactServiceImpl implements ContactService {

    private final static Logger logger = LoggerFactory.getLogger(TailorMadePackageService.class);

    private final ContactRepository contactRepository;

    private final ObjectMapper objectMapper;

    private final AuditLogServiceImpl auditLogService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveContactDetails(CreateContactDto createContactDto, AuditLog auditLog) throws CreateContactException {
        logger.info("save contact details");
        auditLog.setLoggedAt(LocalDateTime.now());
        auditLog.setAction(AuditLogAction.ADD_CONTACT);
        try{

            Contact contact = objectMapper.convertValue(createContactDto, Contact.class);

            Contact savedContact = contactRepository.save(contact);

            this.contactRepository.save(savedContact);
            logger.info("save contact {}", savedContact);
            auditLog.setStatus(AuditLogStatus.SUCCESS);

        }catch (Exception e){
            logger.error("unknown error occur when saving contact: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            throw new CreateContactException(ResponseCode.INTERNAL_SERVER_ERROR);
        }finally{
            auditLogService.save(auditLog);
        }
    }

    @Override
    public List<ContactResponseDto> findAll() throws ResourceNotFoundException {
        logger.info("find All");
        try{
            List<ContactResponseDto> contactResponseList = new ArrayList<>();
            List<Contact> contacts = this.contactRepository.findAll();

            if (contacts.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("No contact details available at the moment");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            for(Contact contactsDetails : contacts){
                ContactResponseDto contactResponseDto = this.objectMapper.convertValue(contactsDetails, ContactResponseDto.class);
                contactResponseList.add(contactResponseDto);
            }

            return contactResponseList;

        }catch (ResourceNotFoundException e){
            logger.error("unknown error occur when finding contact details: {}", e.getResponseCode());
            throw new ResourceNotFoundException(e.getResponseCode());
        }catch (Exception e){
            logger.error("unknown error occur when fetching all tailor contact details: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ContactResponseDto findAllById(String contactId) throws ResourceNotFoundException{
        logger.info("findAllById");
        try{
            Optional<Contact> contacts = this.contactRepository.findById(contactId);

            if (contacts.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("Contact details are not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            return objectMapper.convertValue(contacts.get(), ContactResponseDto.class);

        } catch (ResourceNotFoundException ex) {
            logger.error("error occur when fetching a contact detail by its id: {}", ex.getMessage());
            throw new ResourceNotFoundException(ex.getResponseCode());
        } catch (Exception e){
            logger.error("unknown error occur when fetching a contact detail by its id: {}", e.getMessage());
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
            throw new ResourceNotFoundException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteContactDetails(String contactId, AuditLog auditLog) throws ResourceDeleteException {
        logger.info("delete contact details");
        try{
            Optional<Contact> contactOptional = this.contactRepository.findById(contactId);
            if (contactOptional.isEmpty()) {
                ResponseCode.DATA_RETRIEVED_ERROR.setReason("contact details is not available");
                throw new ResourceNotFoundException(ResponseCode.DATA_RETRIEVED_ERROR);
            }

            Contact contacts = contactOptional.get();

            this.contactRepository.delete(contacts);
        }catch (ResourceDeleteException e){
            logger.error("error occur when deleting a contacts: {}", e.getMessage());
            auditLog.setStatus(AuditLogStatus.FAILED);
            ResponseCode.INTERNAL_SERVER_ERROR.setReason(e.getMessage());
        }
        finally{
            this.auditLogService.save(auditLog);
        }
    }

}
