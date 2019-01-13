package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;

import java.util.Date;


public class APIKeyOS extends PrincipalOS {

    private String secretKey;
    private String name;
    private String userId;
    private boolean isActive;

    public APIKeyOS(PrincipalId id, Date creationTime,
                    String secretKey, String name, String userId, boolean isActive) {
        super(id, PrincipalType.API_KEY, creationTime);
        this.secretKey = secretKey;
        this.name = name;
        this.userId = userId;
        this.isActive = isActive;
    }

    public String getSecretKey() {
        return secretKey;
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
