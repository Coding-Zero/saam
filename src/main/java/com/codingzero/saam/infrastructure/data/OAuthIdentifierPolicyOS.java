package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Date;
import java.util.Map;


public class OAuthIdentifierPolicyOS {

    private String applicationId;
    private OAuthPlatform platform;
    private Map<String, Object> configurations;
    private boolean isActive;
    private long creationTime;
    private long updateTime;

    public OAuthIdentifierPolicyOS(String applicationId, OAuthPlatform platform,
                                   Map<String, Object> configurations, boolean isActive,
                                   Date creationTime, Date updateTime) {
        this.applicationId = applicationId;
        this.platform = platform;
        setConfigurations(configurations);
        this.isActive = isActive;
        this.creationTime = creationTime.getTime();
        setUpdateTime(updateTime);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public OAuthPlatform getPlatform() {
        return platform;
    }

    public Map<String, Object> getConfigurations() {
        return configurations;
    }

    public boolean isActive() {
        return isActive;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public void setConfigurations(Map<String, Object> configurations) {
        this.configurations = Collections.unmodifiableMap(configurations);
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getUpdateTime() {
        return new Date(updateTime);
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime.getTime();
    }
}
