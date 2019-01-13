package com.codingzero.saam.app.requests;

public class UsernamePolicyAddRequest {

    private String applicationId;

    public UsernamePolicyAddRequest(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }

}
