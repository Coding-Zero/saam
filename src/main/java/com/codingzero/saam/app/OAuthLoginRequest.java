package com.codingzero.saam.app;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Map;


public class OAuthLoginRequest {

    private String applicationId;
    private OAuthPlatform platform;
    private String identifier;
    private Map<String, Object> sessionDetails;
    private long sessionTimeout;

    public OAuthLoginRequest(String applicationId, OAuthPlatform platform, String identifier,
                             Map<String, Object> sessionDetails, long sessionTimeout) {
        this.applicationId = applicationId;
        this.platform = platform;
        this.identifier = identifier;
        this.sessionDetails = Collections.unmodifiableMap(sessionDetails);
        this.sessionTimeout = sessionTimeout;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public OAuthPlatform getPlatform() {
        return platform;
    }

    public String getIdentifier() {
        return identifier;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }

    public Map<String, Object> getSessionDetails() {
        return sessionDetails;
    }
}
