package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.OAuthIdentifierKey;

import java.util.Collections;
import java.util.Date;
import java.util.Map;


public class OAuthIdentifierOS {

    private OAuthIdentifierKey key;
    private String userId;
    private Map<String, Object> properties;
    private long creationTime;
    private long updateTime;

    public OAuthIdentifierOS(OAuthIdentifierKey key, String userId,
                             Map<String, Object> properties, Date creationTime, Date updateTime) {
        this.key = key;
        this.userId = userId;
        setProperties(properties);
        this.creationTime = creationTime.getTime();
        setUpdateTime(updateTime);
    }

    public OAuthIdentifierKey getKey() {
        return key;
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
