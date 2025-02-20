package com.tuskerlanka.tourismmangement.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryResponseDto {

    private String heading;

    private String startDate;

    private String endDate;

    private String description;
}
