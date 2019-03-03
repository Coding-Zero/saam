package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.Map;

public abstract class OAuthLoginRequestModel {

    public OAuthLoginRequestModel(@JsonProperty("applicationId") String applicationId,
                                  @JsonProperty("platform") OAuthPlatform platform,
                                  @JsonProperty("identifier") String identifier,
                                  @JsonProperty("details") Map<String, String> details,
                                  @JsonProperty("timeout") long timeout) {}
    
}
