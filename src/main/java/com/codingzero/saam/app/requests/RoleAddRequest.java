package com.codingzero.saam.app.requests;


public class RoleAddRequest {

    private String applicationId;
    private String name;

    public RoleAddRequest(String applicationId, String name) {
        this.applicationId = applicationId;
        this.name = name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getName() {
        return name;
    }
}
