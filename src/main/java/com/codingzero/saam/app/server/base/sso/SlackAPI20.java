package com.codingzero.saam.app.server.base.sso;

import com.github.scribejava.apis.google.GoogleJsonTokenExtractor;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;


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

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return GoogleJsonTokenExtractor.instance();
    }
}
