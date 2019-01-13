package com.codingzero.saam.app.requests;

import com.codingzero.saam.common.OAuthPlatform;


public class OAuthIdentifierDisconnectRequest {

    private String applicationId;
    private String userId;
    private OAuthPlatform platform;
    private String identifier;

    public OAuthIdentifierDisconnectRequest(String applicationId, String userId,
                                            OAuthPlatform platform, String identifier) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.platform = platform;
        this.identifier = identifier;
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
}
