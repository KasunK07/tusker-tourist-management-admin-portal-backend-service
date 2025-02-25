package com.tuskerlanka.tourismmangement.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTailorMadePackageDto {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    private String phone;

    @NotBlank(message = "Country name is required.")
    private String country;

    @NotBlank(message = "Budget is required.")
    private String budget;

    @NotBlank(message = "departure date is required.")
    private LocalDate departureDate;

    @NotBlank(message = "arrival date is required.")
    private LocalDate arrivalDate;

    @NotBlank(message = "Number Of Nights are required.")
    private int numberOfNights;

    @NotBlank(message = "Flexibility On Dates are required.")
    private String flexibilityOnDates;

    @Valid
    private NoOfAdultsDto noOfAdults;

    @Valid
    private NoOfChildrenDto noOfChildren;

    @Valid
    private HotelTypesDto hotelTypes;

    @NotBlank(message = "Holiday Interests are required.")
    private List<String> holidayInterest;

    @NotBlank(message = "Special Requirements are required.")
    @Size(max = 800, min = 10, message = "Description has to be more than 10 characters and less than 800 characters.")
    private String specialRequirements;


}
