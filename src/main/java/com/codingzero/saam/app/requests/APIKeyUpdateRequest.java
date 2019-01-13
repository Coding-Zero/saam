package com.codingzero.saam.app.requests;


public class APIKeyUpdateRequest {

    private String applicationId;
    private String id;
    private String name;
    private boolean isActive;

    public APIKeyUpdateRequest(String applicationId, String id, String name, boolean isActive) {
        this.applicationId = applicationId;
        this.id = id;
        this.name = name;
        this.isActive = isActive;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }
}
