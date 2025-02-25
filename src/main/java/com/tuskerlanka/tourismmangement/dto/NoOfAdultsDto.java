package com.tuskerlanka.tourismmangement.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoOfAdultsDto {

    @NotBlank(message = "Required")
    private String ageGroupOne;

    @NotBlank(message = "Required")
    private String ageGroupTwo;

    @NotBlank(message = "Required")
    private String ageGroupThree;
}
