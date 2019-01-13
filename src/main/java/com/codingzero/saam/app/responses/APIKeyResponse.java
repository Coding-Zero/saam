package com.codingzero.saam.app.responses;


public class APIKeyResponse {

    private String applicationId;
    private String id;
    private String secretKey;
    private String name;
    private String userId;
    private boolean isActive;

    public APIKeyResponse(String applicationId,
                          String id,
                          String secretKey,
                          String name,
                          String userId,
                          boolean isActive) {
        this.applicationId = applicationId;
        this.id = id;
        this.secretKey = secretKey;
        this.name = name;
        this.userId = userId;
        this.isActive = isActive;
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

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isActive() {
        return isActive;
    }

}
