package com.codingzero.saam.app;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Map;


public class OAuthIdentifierConnectRequest {

    private String applicationId;
    private String userId;
    private OAuthPlatform platform;
    private String identifier;
    private Map<String, Object> properties;

    public OAuthIdentifierConnectRequest(String applicationId, String userId, OAuthPlatform platform,
                                         String identifier,
                                         Map<String, Object> properties) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.platform = platform;
        this.identifier = identifier;
        this.properties = Collections.unmodifiableMap(properties);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public OAuthPlatform getPlatform() {
        return platform;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
