package com.codingzero.saam.infrastructure;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Date;


public class SSOAccessToken {

    private OAuthPlatform platform;
    private String accountId;
    private String token;
    private long creationTime;
    private long expirationTime;

    public SSOAccessToken(OAuthPlatform platform, String accountId, String token,
                          Date creationTime, Date expirationTime) {
        this.platform = platform;
        this.accountId = accountId;
        this.token = token;
        this.creationTime = creationTime.getTime();
        this.expirationTime = expirationTime.getTime();
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
