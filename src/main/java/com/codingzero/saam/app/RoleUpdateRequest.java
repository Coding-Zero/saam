package com.codingzero.saam.app;


public class RoleUpdateRequest {

    private String applicationId;
    private String id;
    private String name;

    public RoleUpdateRequest(String applicationId, String id, String name) {
        this.applicationId = applicationId;
        this.id = id;
        this.name = name;
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

}
