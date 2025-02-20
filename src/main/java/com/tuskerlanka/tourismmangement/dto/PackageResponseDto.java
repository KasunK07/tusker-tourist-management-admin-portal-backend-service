package com.tuskerlanka.tourismmangement.dto;

import com.tuskerlanka.tourismmangement.enums.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PackageResponseDto {

    private String id;

    private String name;

    private String description;

    private PackageStatus Status;

    private double price;

    private String currency;

    private double rating;

    private List<PackageImageResponseDto> packageImageDetails;

    private List<ItineraryResponseDto> itineraryDetails;

    private List<String> highlights;
}
