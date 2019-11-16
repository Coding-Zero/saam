package com.codingzero.saam.app.server.infrastructure.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;


public class SlackAPI20 extends DefaultApi20 {

    protected SlackAPI20() {
    }

    private static class InstanceHolder {
        private static final SlackAPI20 INSTANCE = new SlackAPI20();
    }

    public static SlackAPI20 instance() {
        return SlackAPI20.InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://slack.com/api/oauth.access";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://slack.com/oauth/authorize";
    }

}
