package com.tuskerlanka.tourismmangement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PackageImageResponseDto {

    private String imageName;

    private String imagePath;

    private String mimeType;

    private byte[] imageData;
}
