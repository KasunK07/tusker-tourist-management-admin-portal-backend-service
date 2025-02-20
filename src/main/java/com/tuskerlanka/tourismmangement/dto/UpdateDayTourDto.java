package com.tuskerlanka.tourismmangement.dto;


import jakarta.mail.search.SearchTerm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateDayTourDto {

    private String name;

    private String description;

    private Set<DayToursImageResponseDto> imageDetails;

}
