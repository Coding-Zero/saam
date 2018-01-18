package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.Map;

public abstract class OAuthIdentifierPolicyAddRequestModel {

    public OAuthIdentifierPolicyAddRequestModel(@JsonProperty("applicationId") String applicationId,
                                                @JsonProperty("platform") OAuthPlatform platform,
                                                @JsonProperty("configurations") Map<String, String> configurations) {}
    
}
