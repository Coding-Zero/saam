package com.codingzero.saam.app.responses;

import com.codingzero.saam.common.PermissionType;


public class PermissionCheckResponse {

    private String applicationId;
    private String principalId;
    private String resourceKey;
    private String actionCode;
    private PermissionType result;

    public PermissionCheckResponse(String applicationId, String principalId, String resourceKey,
                                   String actionCode, PermissionType result) {
        this.applicationId = applicationId;
        this.principalId = principalId;
        this.resourceKey = resourceKey;
        this.actionCode = actionCode;
        this.result = result;
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

    public PermissionType getResult() {
        return result;
    }
}
