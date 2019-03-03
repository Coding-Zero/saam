package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.common.Action;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class PermissionOS {

    private String applicationId;
    private String resourceKey;
    private String principalId;
    private long creationTime;
    private List<Action> actions;

    public PermissionOS(String applicationId, String resourceKey, String principalId,
                        Date creationTime, List<Action> actions) {
        this.applicationId = applicationId;
        this.principalId = principalId;
        this.resourceKey = resourceKey;
        this.creationTime = creationTime.getTime();
        setActions(actions);
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

    public void setActions(List<Action> actions) {
        this.actions = Collections.unmodifiableList(actions);
    }
}
