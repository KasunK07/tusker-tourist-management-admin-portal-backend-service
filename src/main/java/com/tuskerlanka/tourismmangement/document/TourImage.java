package com.tuskerlanka.tourismmangement.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "day_tours_images")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TourImage {

    @Id
    private String id;

    private String imageName;

    private String imageType;

    private String imagePath;

    private String dayToursId;

    private String mimeType;

}
