package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.Map;

public abstract class OAuthIdentifierConnectRequestModel {

    public OAuthIdentifierConnectRequestModel(@JsonProperty("applicationId") String applicationId,
                                              @JsonProperty("userId") String userId,
                                              @JsonProperty("platform") OAuthPlatform platform,
                                              @JsonProperty("identifier") String identifier,
                                              @JsonProperty("properties") Map<String, String> properties) {}
    
}
