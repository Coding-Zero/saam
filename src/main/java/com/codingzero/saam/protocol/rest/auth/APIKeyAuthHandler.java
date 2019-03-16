package com.codingzero.saam.protocol.rest.auth;

/**
 * Created by ruisun on 2017-01-01.
 */
public class APIKeyAuthHandler implements AuthHandler {

    private String apiKey;

    public APIKeyAuthHandler(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getType() {
        return "APIKey";
    }

    @Override
    public boolean verify(AuthContext ctx) {
        return ctx.getToken().equals(apiKey);
    }
}
