package com.codingzero.saam.presentation.mixin;

import com.codingzero.saam.common.OAuthPlatform;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public abstract class OAuthIdentifierUpdateRequestModel {

    public OAuthIdentifierUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                             @JsonProperty("userId") String userId,
                                             @JsonProperty("platform") OAuthPlatform platform,
                                             @JsonProperty("identifier") String identifier,
                                             @JsonProperty("properties") Map<String, String> properties) {}
    
}
