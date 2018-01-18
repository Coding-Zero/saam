package com.codingzero.saam.app;


public class UsernamePolicyUpdateRequest {

    private String applicationId;
    private String code;
    private boolean isActive;

    public UsernamePolicyUpdateRequest(String applicationId, String code, boolean isActive) {
        this.applicationId = applicationId;
        this.code = code;
        this.isActive = isActive;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getCode() {
        return code;
    }

    public boolean isActive() {
        return isActive;
    }
}
