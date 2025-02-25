package com.tuskerlanka.tourismmangement.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "destination_images")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DestinationImage {

    @Id
    private String id;

    private String imageName;

    private String imageType;

    private String imagePath;

    private String destinationId;

    private String mimeType;
}
