package com.tuskerlanka.tourismmangement.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DayToursImageResponseDto {

    private String imageName;

    private String imagePath;

    private byte[] imageData;

    private String mimeType;
}
