package com.codingzero.saam.app.requests;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Map;


public class OAuthIdentifierPolicyAddRequest {

    private String applicationId;
    private OAuthPlatform platform;
    private Map<String, Object> configurations;

    public OAuthIdentifierPolicyAddRequest(String applicationId, OAuthPlatform platform,
                                           Map<String, Object> configurations) {
        this.applicationId = applicationId;
        this.platform = platform;
        this.configurations = Collections.unmodifiableMap(configurations);
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
}
