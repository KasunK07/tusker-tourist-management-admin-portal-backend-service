package com.tuskerlanka.tourismmangement.constants;

public interface ApplicationConstants {

    String USER_SYSTEM = "SYSTEM";
    String TOKEN_ISSUER = "Tusker Tours";
    String TOKEN_SUBJECT = "Jason Web Token";
    long ACCESS_TOKEN_EXPIRY = 60 * 60 * 1000; // 3,600,000 milliseconds 1 hour
    long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000L; // 604,800,000 milliseconds (7 days)
}
