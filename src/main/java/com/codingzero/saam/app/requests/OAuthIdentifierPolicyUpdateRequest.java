package com.codingzero.saam.app.requests;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Map;


public class OAuthIdentifierPolicyUpdateRequest {

    private String applicationId;
    private OAuthPlatform platform;
    private Map<String, Object> configurations;
    private boolean isActive;

    public OAuthIdentifierPolicyUpdateRequest(String applicationId, OAuthPlatform platform,
                                              Map<String, Object> configurations, boolean isActive) {
        this.applicationId = applicationId;
        this.platform = platform;
        this.configurations = Collections.unmodifiableMap(configurations);
        this.isActive = isActive;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public OAuthPlatform getPlatform() {
        return platform;
    }

    public Map<String, Object> getConfigurations() {
        return configurations;
    }

    public boolean isActive() {
        return isActive;
    }
}
