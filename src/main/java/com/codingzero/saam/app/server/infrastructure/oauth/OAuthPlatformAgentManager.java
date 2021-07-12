package com.codingzero.saam.app.server.infrastructure.oauth;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.oauth.OAuthPlatformAgent;

import java.util.Collections;
import java.util.Map;


public class OAuthPlatformAgentManager {

    private final Map<OAuthPlatform, OAuthPlatformAgent> agents;

    public OAuthPlatformAgentManager(Map<OAuthPlatform, OAuthPlatformAgent> agents) {
        this.agents = Collections.unmodifiableMap(agents);
    }

    public OAuthPlatformAgent get(OAuthPlatform platform) {
        return agents.get(platform);
    }

}
