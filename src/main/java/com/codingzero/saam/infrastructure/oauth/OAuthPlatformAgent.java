package com.codingzero.saam.infrastructure.oauth;

import java.util.Map;

public interface OAuthPlatformAgent {

    String getAuthorizationUrl(Map<String, Object> configurations,
                               Map<String, Object> parameters);

    OAuthAccessToken requestAccessToken(Map<String, Object> configurations,
                                        Map<String, Object> parameters);

}
