package com.codingzero.saam.app.server.infrastructure.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class AppleApi extends DefaultApi20 {

    private AppleApi() {
    }

    private static class InstanceHolder {
        private static final AppleApi INSTANCE = new AppleApi();
    }

    public static AppleApi instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://appleid.apple.com/auth/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://appleid.apple.com/auth/authorize";
    }
}
