package com.codingzero.saam.app;

import com.codingzero.saam.common.Action;

import java.util.Collections;
import java.util.List;


public class PermissionStoreRequest {

    private String applicationId;
    private String resourceKey;
    private String principalId;
    private List<Action> actions;

    public PermissionStoreRequest(String applicationId, String resourceKey, String principalId,
                                  List<Action> actions) {
        this.applicationId = applicationId;
        this.resourceKey = resourceKey;
        this.principalId = principalId;
        this.actions = Collections.unmodifiableList(actions);
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

    public List<Action> getActions() {
        return actions;
    }
}
