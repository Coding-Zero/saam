package com.codingzero.saam.app;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.Map;


public class OAuthAuthorizationUrlRequest {

    private String applicationId;
    private OAuthPlatform platform;
    private Map<String, Object> parameters;

    public OAuthAuthorizationUrlRequest(String applicationId, OAuthPlatform platform, Map<String, Object> parameters) {
        this.applicationId = applicationId;
        this.platform = platform;
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public OAuthPlatform getPlatform() {
        return platform;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "OAuthAuthorizationUrlRequest{" +
                "applicationId='" + applicationId + '\'' +
                ", platform=" + platform +
                ", parameters=" + parameters +
                '}';
    }
}
