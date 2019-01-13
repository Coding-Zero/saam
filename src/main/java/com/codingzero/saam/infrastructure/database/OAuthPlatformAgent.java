package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.OAuthAccessToken;

import java.util.Map;

public interface OAuthPlatformAgent {

    String getAuthorizationUrl(OAuthPlatform platform,
                               Map<String, Object> configurations,
                               Map<String, Object> parameters);

    OAuthAccessToken requestAccessToken(OAuthPlatform platform,
                                        Map<String, Object> configurations,
                                        Map<String, Object> parameters);

}
