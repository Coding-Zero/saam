package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.PrincipalType;

import java.util.Date;


public class APIKeyOS extends PrincipalOS {

    private String key;
    private String name;
    private String userId;
    private boolean isActive;

    public APIKeyOS(String applicationId, String id, Date creationTime,
                    String key, String name, String userId, boolean isActive) {
        super(applicationId, id, PrincipalType.API_KEY, creationTime);
        this.key = key;
        this.name = name;
        this.userId = userId;
        this.isActive = isActive;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
