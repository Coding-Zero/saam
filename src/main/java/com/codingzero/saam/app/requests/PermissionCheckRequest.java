package com.codingzero.saam.app.requests;


public class PermissionCheckRequest {

    private String applicationId;
    private String resourceKey;
    private String principalId;
    private String actionCode;

    public PermissionCheckRequest(String applicationId,
                                  String resourceKey,
                                  String principalId,
                                  String actionCode) {
        this.applicationId = applicationId;
        this.principalId = principalId;
        this.resourceKey = resourceKey;
        this.actionCode = actionCode;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public String getActionCode() {
        return actionCode;
    }

}
