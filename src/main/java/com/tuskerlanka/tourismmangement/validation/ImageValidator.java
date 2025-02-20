package com.tuskerlanka.tourismmangement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator implements ConstraintValidator<ImageFile, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null) {
            return false;
        }

        // Validating file type
        final String fileContentType = file.getContentType();
        if (!fileContentType.equals("image/jpeg") &&
                !fileContentType.equals("image/jpg") &&
                !fileContentType.equals("image/png")) {
            return false;
        }

        // Validating file extension
        final String fileName = file.getOriginalFilename();
        return fileName.toLowerCase().matches(".*\\.(jpeg|jpg|png)$");
    }
}
