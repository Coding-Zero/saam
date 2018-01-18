package com.codingzero.saam.app;


public class APIKeyAddRequest {

    private String applicationId;
    private String userId;
    private String name;

    public APIKeyAddRequest(String applicationId, String userId, String name) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.name = name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
