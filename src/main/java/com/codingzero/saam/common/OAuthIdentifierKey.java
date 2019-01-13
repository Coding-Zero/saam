package com.codingzero.saam.common;

public class OAuthIdentifierKey {

    private String applicationId;
    private OAuthPlatform platform;
    private String content;

    public OAuthIdentifierKey(String applicationId, OAuthPlatform platform, String content) {
        this.applicationId = applicationId;
        this.platform = platform;
        this.content = content;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public OAuthPlatform getPlatform() {
        return platform;
    }

    public String getContent() {
        return content;
    }
}
