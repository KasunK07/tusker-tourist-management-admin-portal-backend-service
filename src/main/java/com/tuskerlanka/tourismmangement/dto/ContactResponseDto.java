package com.tuskerlanka.tourismmangement.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseDto {

    private String name;

    private String email;

    private String phone;

    private String description;

}
