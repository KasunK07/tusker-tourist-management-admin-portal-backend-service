package com.tuskerlanka.tourismmangement.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateContactDto {


    @NotBlank(message = "Tour name is required.")
    private String name;

    @NotBlank(message = "Email is required.")
    private String email;


    @NotBlank(message = "Phone is required.")
    private String phone;


    @NotBlank(message = "A description is required")
    @Size(max = 500, message = "Description has to be less than 800 characters.")
    @Size(min = 10, message = "Description has to be more than 10 characters.")
    private String description;
}
