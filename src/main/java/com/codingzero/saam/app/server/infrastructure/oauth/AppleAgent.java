package com.codingzero.saam.app.server.infrastructure.oauth;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.oauth.OAuthAccessToken;
import com.codingzero.saam.infrastructure.oauth.OAuthPlatformAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.Map;

public class AppleAgent implements OAuthPlatformAgent {

    private static final String JWT_AUD = "https://appleid.apple.com";
    private static final Long JWT_TOKEN_TIME_OUT = 1000L * 60L * 60L * 24L * 90L; //90 days

    private final OAuth20ServiceFactory oAuth20ServiceFactory;
    private final ObjectMapper objectMapper;
    private final OAuthPlatformAgentHelper helper;

    public AppleAgent() {
        this(new OAuth20ServiceFactory(), new ObjectMapper(), new OAuthPlatformAgentHelper());
    }
    public AppleAgent(OAuth20ServiceFactory oAuth20ServiceFactory,
                      ObjectMapper objectMapper,
                      OAuthPlatformAgentHelper helper) {
        this.oAuth20ServiceFactory = oAuth20ServiceFactory;
        this.objectMapper = objectMapper;
        this.helper = helper;
    }

    @Override
    public String getAuthorizationUrl(Map<String, Object> configurations, Map<String, Object> parameters) {
        configurations = helper.prepareConfiguration(configurations, parameters);
        Map<String, String> newParameters = helper.prepareParametersForAuthUrl(parameters);
        OAuth20Service service = oAuth20ServiceFactory.generate(OAuthPlatform.APPLE, configurations);
        return service.getAuthorizationUrl(newParameters);
    }

    @Override
    public OAuthAccessToken requestAccessToken(Map<String, Object> configurations, Map<String, Object> parameters) {
        return null;
    }
}
