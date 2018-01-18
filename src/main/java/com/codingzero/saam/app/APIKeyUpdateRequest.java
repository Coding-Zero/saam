package com.codingzero.saam.app;


public class APIKeyUpdateRequest {

    private String applicationId;
    private String key;
    private String name;
    private boolean isActive;

    public APIKeyUpdateRequest(String applicationId, String key, String name, boolean isActive) {
        this.applicationId = applicationId;
        this.key = key;
        this.name = name;
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

    public boolean isActive() {
        return isActive;
    }
}
