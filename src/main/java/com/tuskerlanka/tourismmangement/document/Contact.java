package com.tuskerlanka.tourismmangement.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Contact {

    @Id
    private String id;

    private String name;

    private String email;

    private String phone;

    private String description;
}
