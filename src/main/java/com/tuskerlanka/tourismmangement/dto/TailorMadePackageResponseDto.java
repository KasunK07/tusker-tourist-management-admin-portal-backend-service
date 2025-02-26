package com.tuskerlanka.tourismmangement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TailorMadePackageResponseDto {

    private String id;

    private String name;

    private String email;

    private String phone;

    private String country;

    private String budget;

    private LocalDate departureDate;

    private LocalDate arrivalDate;

    private int numberOfNights;

    private String flexibilityOnDates;

    private NoOfAdultsDto noOfAdults;

    private NoOfChildrenDto noOfChildren;

    private HotelTypesDto hotelTypes;

    private List<String> holidayInterest;

    private String specialRequirements;
}
