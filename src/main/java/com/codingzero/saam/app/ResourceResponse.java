package com.codingzero.saam.app;

import com.codingzero.saam.common.Action;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ResourceResponse {

    private String applicationId;
    private String parentKey;
    private String key;
    private String ownerId;
    private long creationTime;
    private boolean isRoot;

    public ResourceResponse(String applicationId, String parentKey, String key,
                            String ownerId, Date creationTime, boolean isRoot) {
        this.applicationId = applicationId;
        this.parentKey = parentKey;
        this.key = key;
        this.ownerId = ownerId;
        this.creationTime = creationTime.getTime();
        this.isRoot = isRoot;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getParentKey() {
        return parentKey;
    }

    public String getKey() {
        return key;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public boolean isRoot() {
        return isRoot;
    }

    public static class Permission {

        private String principalId;
        private long creationTime;
        private List<Action> actions;

        public Permission(String principalId, Date creationTime, List<Action> actions) {
            this.principalId = principalId;
            this.creationTime = creationTime.getTime();
            this.actions = Collections.unmodifiableList(actions);
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

}
