package com.codingzero.saam.app.requests;


public class UsernamePolicyUpdateRequest {

    private String applicationId;
    private boolean isActive;

    public UsernamePolicyUpdateRequest(String applicationId, boolean isActive) {
        this.applicationId = applicationId;
        this.isActive = isActive;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public boolean isActive() {
        return isActive;
    }
}
