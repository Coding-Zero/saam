package com.codingzero.saam.infrastructure.database;

public class PermissionResourceIndex {

    private String applicationId;
    private String resourceKey;
    private String principalId;

    public PermissionResourceIndex(String applicationId, String resourceKey, String principalId) {
        this.applicationId = applicationId;
        this.resourceKey = resourceKey;
        this.principalId = principalId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public String getPrincipalId() {
        return principalId;
    }
}
