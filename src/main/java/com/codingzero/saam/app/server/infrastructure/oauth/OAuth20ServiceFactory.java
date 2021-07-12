package com.codingzero.saam.app.server.infrastructure.oauth;

import com.codingzero.saam.common.OAuthPlatform;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.Map;
import java.util.Objects;


public class OAuth20ServiceFactory {

    public OAuth20Service generate(OAuthPlatform platform, Map<String, Object> configuration) {
        if (platform == OAuthPlatform.GOOGLE) {
            return generateGoogleService(configuration);
        }
        if (platform == OAuthPlatform.SLACK) {
            return generateSlackService(configuration);
        }
        if (platform == OAuthPlatform.APPLE) {
            return generateAppleService(configuration);
        }
        throw new IllegalArgumentException("Unsupported platform, " + platform);
    }

    private OAuth20Service generateSlackService(Map<String, Object> configuration) {
        return new ServiceBuilder(((String) configuration.get("apiKey")))
                .apiSecret((String) configuration.get("apiSecret"))
                .defaultScope((String) configuration.get("scope"))
                .callback((String) configuration.get("callback"))
                .build(SlackAPI20.instance());
    }

    private OAuth20Service generateGoogleService(Map<String, Object> configuration) {
        return new ServiceBuilder(((String) configuration.get("apiKey")))
                .apiSecret((String) configuration.get("apiSecret"))
                .defaultScope((String) configuration.get("scope"))
                .callback((String) configuration.get("callback"))
                .build(GoogleApi20.instance());
    }

    private OAuth20Service generateAppleService(Map<String, Object> configuration) {
        ServiceBuilder builder = new ServiceBuilder(((String) configuration.get("apiKey")))
                .callback((String) configuration.get("callback"));
        if (configuration.containsKey("apiSecret")
                && !Objects.isNull(configuration.get("apiSecret"))) {
            builder.apiSecret((String) configuration.get("apiSecret"));
        }
        if (configuration.containsKey("scope")
                && !Objects.isNull(configuration.get("scope"))) {
            builder.defaultScope((String) configuration.get("scope"));
        }
        return builder.build(AppleApi.instance());
    }

}
