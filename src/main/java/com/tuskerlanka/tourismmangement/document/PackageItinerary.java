package com.tuskerlanka.tourismmangement.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "package_itinerary")
public class PackageItinerary {

    private String id;

    private String heading;

    private String startDate;

    private String endDate;

    private String description;

    private String packageId;
}
