package com.tuskerlanka.tourismmangement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDayToursDto {

    @NotBlank(message = "Tour name is required.")
    private String name;

    @NotBlank(message = "A description is required")
    @Size(max = 500, message = "Description has to be less than 500 characters.")
    @Size(min = 10, message = "Description has to be more than 10 characters.")
    private String description;


    private List<DayToursImageResponseDto> images;
}
