package com.tuskerlanka.tourismmangement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateDestinationsDto {

    private String name;

    private String description;

    private Set<DestinationImageResponseDto> imageDetails;
}
