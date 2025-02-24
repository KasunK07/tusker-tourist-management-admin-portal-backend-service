package com.tuskerlanka.tourismmangement.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseDocument {

    @CreatedDate
    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @CreatedBy
    @Field(name = "created_by")
    private String createdBy;

    @LastModifiedDate
    @Field(name = "updated_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Field(name = "updated_by")
    private String updatedBy;
}
