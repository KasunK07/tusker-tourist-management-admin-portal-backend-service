package com.tuskerlanka.tourismmangement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateItineraryDto {

    @NotBlank(message = "Itinerary heading is required.")
    private String heading;

    @NotBlank(message = "Start date is required.")
    private String startDate;

    @NotBlank(message = "End date is required.")
    private String endDate;

    @NotBlank(message = "Itinerary description is required.")
    private String description;

}
