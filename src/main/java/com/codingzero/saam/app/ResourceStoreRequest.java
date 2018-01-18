package com.codingzero.saam.app;


public class ResourceStoreRequest {

    private String applicationId;
    private String userId;
    private String key;

    public ResourceStoreRequest(String applicationId, String userId, String key) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.key = key;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getKey() {
        return key;
    }

}
