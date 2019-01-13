package com.codingzero.saam.common;

public class PrincipalId {

    private String applicationId;
    private String id;

    public PrincipalId(String applicationId, String id) {
        this.applicationId = applicationId;
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getId() {
        return id;
    }
}
