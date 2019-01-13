package com.codingzero.saam.app.responses;

import java.util.Date;


public class RoleResponse {

    private String applicationId;
    private String id;
    private long creationTime;
    private String name;

    public RoleResponse(String applicationId, String id, Date creationTime, String name) {
        this.applicationId = applicationId;
        this.id = id;
        this.creationTime = creationTime.getTime();
        this.name = name;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getId() {
        return id;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public String getName() {
        return name;
    }

}
