package com.codingzero.saam.infrastructure.database;

import java.util.Collections;
import java.util.Date;
import java.util.Map;


public class UserSessionOS {

    private String applicationId;
    private String key;
    private String userId;
    private long expirationTime;
    private long creationTime;
    private Map<String, Object> details;

    public UserSessionOS(String applicationId, String key, String userId,
                         Date expirationTime, Date creationTime, Map<String, Object> details) {
        this.applicationId = applicationId;
        this.key = key;
        this.expirationTime = expirationTime.getTime();
        this.creationTime = creationTime.getTime();
        this.userId = userId;
        this.details = Collections.unmodifiableMap(details);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getKey() {
        return key;
    }

    public String getUserId() {
        return userId;
    }

    public Date getExpirationTime() {
        return new Date(expirationTime);
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
