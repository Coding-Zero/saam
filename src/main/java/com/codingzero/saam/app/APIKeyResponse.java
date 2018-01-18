package com.codingzero.saam.app;


public class APIKeyResponse {

    private String applicationId;
    private String key;
    private String name;
    private String userId;
    private boolean isActive;

    public APIKeyResponse(String applicationId, String key, String name, String userId, boolean isActive) {
        this.applicationId = applicationId;
        this.key = key;
        this.name = name;
        this.userId = userId;
        this.isActive = isActive;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getKey() {
        return key;
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
