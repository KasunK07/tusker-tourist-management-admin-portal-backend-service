package com.tuskerlanka.tourismmangement.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Token {

    @Id
    private String id;

    private String jti;

    @Field(name = "expiry_date")
    private Date expiryDate;

    private boolean revoked = false;

    public void revoke() {
        this.revoked = true;
    }
}
