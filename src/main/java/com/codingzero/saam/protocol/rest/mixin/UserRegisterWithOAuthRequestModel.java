package com.codingzero.saam.protocol.rest.mixin;

import com.codingzero.saam.app.requests.UserRegisterWithOAuthRequest;
import com.codingzero.saam.common.OAuthPlatform;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public abstract class UserRegisterWithOAuthRequestModel {

    public UserRegisterWithOAuthRequestModel(@JsonProperty("applicationId") String applicationId,
                                             @JsonProperty("oAuthIdentifiers") Map<OAuthPlatform, UserRegisterWithOAuthRequest.OAuthIdentifier> oAuthIdentifiers,
                                             @JsonProperty("roleIds") List<String> roleIds) {}
    
}
