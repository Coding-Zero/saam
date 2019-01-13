package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.ResourceKeySeparator;

import java.util.Date;


public class ResourceOS {

    private String applicationId;
    private String key; // path to root, eg: root:name1:name2:name
    private String principalId;
    private long creationTime;
    private String parentKey; // path to root, eg: root:name1:name2

    public ResourceOS(String applicationId, String key, String parentKey, String principalId, Date creationTime) {
        checkForIllegalParentKey(key, parentKey);
        this.applicationId = applicationId;
        this.parentKey = parentKey;
        this.key = key;
        this.principalId = principalId;
        this.creationTime = creationTime.getTime();
    }

    private void checkForIllegalParentKey(String key, String parentKey) {
        if (null == parentKey) {
            return;
        }
        if (key.length() <= parentKey.length()) {
            throw new IllegalArgumentException("Illegal parent key, " + parentKey + " for given key, " + key);
        }
        int parentKeyPosition = key.indexOf(parentKey) + 1;
        int lastIndexOfSeparator = key.lastIndexOf(ResourceKeySeparator.VALUE);
        if (parentKeyPosition != lastIndexOfSeparator) {
            throw new IllegalArgumentException("Illegal parent key, " + parentKey + " for given key, " + key);
        }
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
