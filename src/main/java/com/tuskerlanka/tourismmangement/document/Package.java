package com.tuskerlanka.tourismmangement.document;

import com.tuskerlanka.tourismmangement.enums.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Package {

    @Id
    private String id;

    private String name;

    private String description;

    private double price;

    private String startDay;

    private String endDay;

    private double rating;

    private List<String> highlights;

    private PackageStatus status;

    private Set<String> imageIds;

    private Set<String> itineraryIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
