package com.codingzero.saam.app.responses;

import com.codingzero.saam.common.Action;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class PermissionResponse {

    private String applicationId;
    private String resourceKey;
    private String principalId;
    private long creationTime;
    private List<Action> actions;

    public PermissionResponse(String applicationId, String resourceKey, String principalId,
                              Date creationTime, List<Action> actions) {
        this.applicationId = applicationId;
        this.principalId = principalId;
        this.resourceKey = resourceKey;
        this.creationTime = creationTime.getTime();
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

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public List<Action> getActions() {
        return actions;
    }
}
