package com.codingzero.saam.app.responses;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Date;


public class OAuthAccessTokenResponse {

    private String applicationId;
    private OAuthPlatform platform;
    private String accountId;
    private String token;
    private long creationTime;
    private long expirationTime;

    public OAuthAccessTokenResponse(String applicationId, OAuthPlatform platform,
                                    String accountId, String token,
                                    Date creationTime, Date expirationTime) {
        this.applicationId = applicationId;
        this.platform = platform;
        this.accountId = accountId;
        this.token = token;
        this.creationTime = creationTime.getTime();
        this.expirationTime = expirationTime.getTime();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public OAuthPlatform getPlatform() {
        return platform;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getToken() {
        return token;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public Date getExpirationTime() {
        return new Date(expirationTime);
    }
}
