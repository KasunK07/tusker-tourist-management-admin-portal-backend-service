package com.tuskerlanka.tourismmangement.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DayTours {

    @Id
    private String id;

    private String name;

    private String description;

    private LocalDateTime createdAt;

    private Set<String> imageIds;

    private String mimeType;
}
