package com.tuskerlanka.tourismmangement.validation.password;

import com.tuskerlanka.tourismmangement.dto.user.CreateUserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, CreateUserDto> {

    @Override
    public boolean isValid(CreateUserDto createUserDto, ConstraintValidatorContext context) {
        if (createUserDto.password() == null || createUserDto.confirmPassword() == null) {
            return false;
        }

        return createUserDto.password().equals(createUserDto.confirmPassword());
    }
}
