package com.codingzero.saam.app;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class OAuthRegisterRequest {

    private String applicationId;
    private OAuthPlatform platform;
    private String identifier;
    private Map<String, Object> properties;
    private List<String> roleIds;

    public OAuthRegisterRequest(String applicationId, OAuthPlatform platform,
                                String identifier, Map<String, Object> properties) {
        this(applicationId, platform, identifier, properties, Collections.emptyList());
    }

    public OAuthRegisterRequest(String applicationId, OAuthPlatform platform,
                                String identifier, Map<String, Object> properties, List<String> roleIds) {
        this.applicationId = applicationId;
        this.platform = platform;
        this.identifier = identifier;
        this.properties = Collections.unmodifiableMap(properties);
        this.roleIds = Collections.unmodifiableList(roleIds);
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }
}
