package com.codingzero.saam.app;

import java.util.Collections;
import java.util.Map;


public class UserSessionCreateRequest {

    private String applicationId;
    private String userId;
    private Map<String, Object> details;
    private long sessionTimeout;

    public UserSessionCreateRequest(String applicationId, String userId,
                                    Map<String, Object> details, long sessionTimeout) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.details = Collections.unmodifiableMap(details);
        this.sessionTimeout = sessionTimeout;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }
}
