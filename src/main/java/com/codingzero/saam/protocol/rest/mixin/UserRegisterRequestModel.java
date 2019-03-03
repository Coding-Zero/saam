package com.codingzero.saam.protocol.rest.mixin;

import com.codingzero.saam.app.requests.UserRegisterRequest;
import com.codingzero.saam.common.OAuthPlatform;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public abstract class UserRegisterRequestModel {

    public UserRegisterRequestModel(@JsonProperty("applicationId") String applicationId,
                                    @JsonProperty("identifiers") Map<String, String> identifiers,
                                    @JsonProperty("oAuthIdentifiers") Map<OAuthPlatform, UserRegisterRequest.OAuthIdentifier> oAuthIdentifiers,
                                    @JsonProperty("password") String password,
                                    @JsonProperty("roleIds") List<String> roleIds) {}
    
}
