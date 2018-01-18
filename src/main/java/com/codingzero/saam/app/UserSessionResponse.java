package com.codingzero.saam.app;

import java.util.Collections;
import java.util.Date;
import java.util.Map;


public class UserSessionResponse {

    private String applicationId;
    private String key;
    private String userId;
    private long expirationTime;
    private long creationTime;
    private boolean isExpired;
    private Map<String, Object> details;

    public UserSessionResponse(String applicationId, String key, String userId,
                               Date expirationTime, Date creationTime, boolean isExpired,
                               Map<String, Object> details) {
        this.applicationId = applicationId;
        this.key = key;
        this.userId = userId;
        this.expirationTime = expirationTime.getTime();
        this.creationTime = creationTime.getTime();
        this.isExpired = isExpired;
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

    public boolean isExpired() {
        return isExpired;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
