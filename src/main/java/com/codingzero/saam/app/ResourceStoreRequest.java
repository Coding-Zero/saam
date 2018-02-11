package com.codingzero.saam.app;


public class ResourceStoreRequest {

    private String applicationId;
    private String ownerId; //principalId
    private String key;

    public ResourceStoreRequest(String applicationId, String ownerId, String key) {
        this.applicationId = applicationId;
        this.ownerId = ownerId;
        this.key = key;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getKey() {
        return key;
    }

}
