package com.tuskerlanka.tourismmangement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelTypesDto {

    @NotBlank(message = "Required")
    private String firstReference;

    @NotBlank(message = "Required")
    private String secondReference;


}
