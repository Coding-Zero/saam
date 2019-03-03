package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.Map;

public abstract class OAuthIdentifierPolicyUpdateRequestModel {

    public OAuthIdentifierPolicyUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                                   @JsonProperty("platform") OAuthPlatform platform,
                                                   @JsonProperty("configurations") Map<String, String> configurations,
                                                   @JsonProperty("active") boolean isActive) {}
    
}
