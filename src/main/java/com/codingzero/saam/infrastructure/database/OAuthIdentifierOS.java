package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Date;
import java.util.Map;


public class OAuthIdentifierOS {

    private String applicationId;
    private OAuthPlatform platform;
    private String content;
    private String userId;
    private Map<String, Object> properties;
    private long creationTime;
    private long updateTime;

    public OAuthIdentifierOS(String applicationId, OAuthPlatform platform, String content, String userId,
                             Map<String, Object> properties, Date creationTime, Date updateTime) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.platform = platform;
        this.content = content;
        setProperties(properties);
        this.creationTime = creationTime.getTime();
        setUpdateTime(updateTime);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public OAuthPlatform getPlatform() {
        return platform;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = Collections.unmodifiableMap(properties);
    }

    public Date getUpdateTime() {
        return new Date(updateTime);
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime.getTime();
    }

}
