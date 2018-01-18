package com.codingzero.saam.app.server.base.sso;

import com.codingzero.saam.common.OAuthPlatform;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.Map;


public class OAuth20ServiceFactory {

    public OAuth20Service generate(OAuthPlatform platform, Map<String, Object> configuration) {
        if (platform == OAuthPlatform.GOOGLE) {
            return generateGoogleService(configuration);
        }
        if (platform == OAuthPlatform.SLACK) {
            return generateSlackService(configuration);
        }
        throw new IllegalArgumentException("Unsupported platform, " + platform);
    }

    private OAuth20Service generateSlackService(Map<String, Object> configuration) {
        return new ServiceBuilder()
                .apiKey((String) configuration.get("apiKey"))
                .apiSecret((String) configuration.get("apiSecret"))
                .scope((String) configuration.get("scope"))
                .callback((String) configuration.get("callback"))
                .build(SlackAPI20.instance());
    }

    private OAuth20Service generateGoogleService(Map<String, Object> configuration) {
        return new ServiceBuilder()
                .apiKey((String) configuration.get("apiKey"))
                .apiSecret((String) configuration.get("apiSecret"))
                .scope((String) configuration.get("scope"))
                .callback((String) configuration.get("callback"))
                .build(GoogleApi20.instance());
    }
}
