package com.tuskerlanka.tourismmangement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class DestinationResponseDto {

    private String id;

    private String name;

    private String description;

    private List<DestinationImageResponseDto> imageDetails;
}
