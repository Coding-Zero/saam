package com.codingzero.saam.app.server.infrastructure.sso;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.OAuthAccessToken;
import com.codingzero.saam.infrastructure.data.OAuthPlatformAgent;

import java.util.Collections;
import java.util.Map;


public class OAuthPlatformAgentManager implements OAuthPlatformAgent {

    private final Map<OAuthPlatform, OAuthPlatformAgent> agents;

    public OAuthPlatformAgentManager(Map<OAuthPlatform, OAuthPlatformAgent> agents) {
        this.agents = Collections.unmodifiableMap(agents);
    }

    @Override
    public String getAuthorizationUrl(OAuthPlatform platform,
                                      Map<String, Object> configurations,
                                      Map<String, Object> parameters) {
        return agents.get(platform).getAuthorizationUrl(platform, configurations, parameters);
    }

    @Override
    public OAuthAccessToken requestAccessToken(
            OAuthPlatform platform, Map<String, Object> configurations, Map<String, Object> parameters) {
        return agents.get(platform).requestAccessToken(platform, configurations, parameters);
    }
}
