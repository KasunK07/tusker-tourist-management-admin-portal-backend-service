package com.tuskerlanka.tourismmangement.dto;

import com.tuskerlanka.tourismmangement.enums.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdatePackageDto {

    private String name;
    private String description;
    private double price;
    private List<String> highlights;
    private List<CreateItineraryDto> itineraryDetails;
    private List<String> imageIdsToKeep;
}
