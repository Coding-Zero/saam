package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.SSOAccessToken;

import java.util.Map;

public interface OAuthPlatformAgent {

    String getAuthorizationUrl(OAuthPlatform platform,
                               Map<String, Object> configurations,
                               Map<String, Object> parameters);

    SSOAccessToken requestAccessToken(OAuthPlatform platform,
                                      Map<String, Object> configurations,
                                      Map<String, Object> parameters);

}
