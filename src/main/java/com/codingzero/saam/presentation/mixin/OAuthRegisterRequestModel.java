package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.OAuthPlatform;

import java.util.List;
import java.util.Map;

public abstract class OAuthRegisterRequestModel {

    public OAuthRegisterRequestModel(@JsonProperty("applicationId") String applicationId,
                                     @JsonProperty("platform") OAuthPlatform platform,
                                     @JsonProperty("identifier") String identifier,
                                     @JsonProperty("properties") Map<String, String> properties,
                                     @JsonProperty("roleIds") List<String> roleIds) {}
    
}
