package com.tuskerlanka.tourismmangement.dto;


import com.tuskerlanka.tourismmangement.enums.PackageStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DayToursResponseDto {

    private String id;

    private String name;

    private String description;

    private DayToursImageResponseDto imageDetails;
}
