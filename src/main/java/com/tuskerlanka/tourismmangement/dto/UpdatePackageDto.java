package com.tuskerlanka.tourismmangement.dto;

import com.tuskerlanka.tourismmangement.enums.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdatePackageDto {

    private String name;

    private String description;

    private double price;

    private String currency;

    private PackageStatus status;
}
