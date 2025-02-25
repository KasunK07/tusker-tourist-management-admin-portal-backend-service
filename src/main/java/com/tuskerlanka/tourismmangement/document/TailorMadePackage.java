package com.tuskerlanka.tourismmangement.document;


import com.tuskerlanka.tourismmangement.dto.HotelTypesDto;
import com.tuskerlanka.tourismmangement.dto.NoOfAdultsDto;
import com.tuskerlanka.tourismmangement.dto.NoOfChildrenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TailorMadePackage {

    @Id
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
