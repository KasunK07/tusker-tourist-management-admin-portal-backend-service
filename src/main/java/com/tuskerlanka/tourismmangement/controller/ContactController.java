package com.tuskerlanka.tourismmangement.controller;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.dto.ContactResponseDto;
import com.tuskerlanka.tourismmangement.dto.CreateContactDto;
import com.tuskerlanka.tourismmangement.enums.ResponseCode;
import com.tuskerlanka.tourismmangement.payload.ApiResponse;
import com.tuskerlanka.tourismmangement.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tuskerlanka.tourismmangement.constants.ApplicationConstants.USER_SYSTEM;

@RestController
@RequestMapping(path = "/api/v1/contact")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class ContactController {

    private final static Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    private final static String END_POINT = "/api/v1/contact";

    @PostMapping(path = "/save")
    public ResponseEntity<ApiResponse> save(@RequestBody CreateContactDto createContactDto){
        logger.info("entering save contact api end point..");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("POST");
        auditLog.setEndPoint(END_POINT+"/save");
        auditLog.setUsername(USER_SYSTEM);

        this.contactService.saveContactDetails(createContactDto, auditLog);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.getInstance(ResponseCode.CONTACT_CREATED_SUCCESSFULLY));
    }

    @GetMapping(path = "getAll")
    public ResponseEntity<ApiResponse> getAll(){
        logger.info("entering getAll contact api end point..");

        List<ContactResponseDto> all = this.contactService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS,all));
    }

    @GetMapping(path = "/get/{contactId}")
    public ResponseEntity<ApiResponse> getById(@PathVariable String contactId){
        logger.info("entering getAll contact api end point..");

        ContactResponseDto response = this.contactService.findAllById(contactId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.DATA_RETRIEVED_SUCCESS, response));
    }

    @DeleteMapping(path = "/delete/{contactId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String contactId){
        logger.info("entering delete contact api end point..");

        AuditLog auditLog = new AuditLog();
        auditLog.setMethod("POST");
        auditLog.setEndPoint(END_POINT+"/delete");
        auditLog.setUsername(USER_SYSTEM);

        this.contactService.deleteContactDetails(contactId,auditLog);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.getInstance(ResponseCode.CONTACT_DELETED_SUCCESSFULLY));
    }

}
