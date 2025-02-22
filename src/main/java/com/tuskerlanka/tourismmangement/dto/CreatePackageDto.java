package com.tuskerlanka.tourismmangement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreatePackageDto {

    @NotBlank(message = "Package name is required.")
    private String name;

    @NotBlank(message = "A description is required")
    @Size(max = 1000, message = "Description has to be less than 500 characters.")
    @Size(min = 10, message = "Description has to be more than 10 characters.")
    private String description;

//    @NotBlank(message = "Price is required.")
    private double price;

    private List<String> highlights;

    @Valid
    private List<CreateItineraryDto> itineraryDetails;
}
