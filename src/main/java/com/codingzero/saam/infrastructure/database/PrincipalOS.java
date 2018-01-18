package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.PrincipalType;

import java.util.Date;


public class PrincipalOS {

    private String applicationId;
    private String id;
    private PrincipalType type;
    private long creationTime;

    public PrincipalOS(String applicationId, String id, PrincipalType type, Date creationTime) {
        this.applicationId = applicationId;
        this.id = id;
        this.type = type;
        this.creationTime = creationTime.getTime();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getId() {
        return id;
    }

    public PrincipalType getType() {
        return type;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }
}
