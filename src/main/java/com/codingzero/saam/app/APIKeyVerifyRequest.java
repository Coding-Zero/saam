package com.codingzero.saam.app;


public class APIKeyVerifyRequest {

    private String applicationId;
    private String id;
    private String secretKey;

    public APIKeyVerifyRequest(String applicationId, String id, String secretKey) {
        this.applicationId = applicationId;
        this.id = id;
        this.secretKey = secretKey;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getId() {
        return id;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
