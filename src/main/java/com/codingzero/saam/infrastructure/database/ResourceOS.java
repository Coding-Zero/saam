package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.ResourceKeySeparator;

import java.util.Date;


public class ResourceOS {

    private String applicationId;
    private String key; // path to root, eg: /root/name1/name2/name
    private String principalId;
    private long creationTime;
    private String parentKey; // path to root, eg: /root/name1/name2

    public ResourceOS(String applicationId, String key, String principalId, Date creationTime) {
        this.applicationId = applicationId;
        this.parentKey = readParentKey(key);
        this.key = key;
        this.principalId = principalId;
        this.creationTime = creationTime.getTime();
    }

    private String readParentKey(String key) {
        String parentKey = null;
        int lastIndexOfSeparator = key.lastIndexOf(ResourceKeySeparator.VALUE);
        if (-1 != lastIndexOfSeparator) {
            parentKey = key.substring(0, lastIndexOfSeparator);
        }
        return parentKey;
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

    public String getPrincipalId() {
        return principalId;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
}
