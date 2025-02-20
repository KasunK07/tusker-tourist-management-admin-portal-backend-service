package com.tuskerlanka.tourismmangement.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "refresh_token")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RefreshToken {

    @Id
    private String id;
    private String jti;
    private Date expiryDate;
    private boolean revoked = false;

    public void revoke() {
        this.revoked = true;
    }
}
