package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.Map;

public abstract class OAuthAccessTokenRequestModel {

    public OAuthAccessTokenRequestModel(@JsonProperty("applicationId") String applicationId,
                                        @JsonProperty("platform") OAuthPlatform platform,
                                        @JsonProperty("parameters") Map<String, String> parameters) {}
    
}
