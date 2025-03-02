package com.tuskerlanka.tourismmangement.validation.image;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
public @interface ImageFile {

    String message() default "Invalid file type. Only JPEG, JPG and PNG files are allowed.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

