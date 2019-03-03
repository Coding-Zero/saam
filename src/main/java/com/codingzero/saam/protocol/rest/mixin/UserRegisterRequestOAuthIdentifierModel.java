package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public abstract class UserRegisterRequestOAuthIdentifierModel {

    public UserRegisterRequestOAuthIdentifierModel(@JsonProperty("identifier") String identifier,
                                                   @JsonProperty("properties") Map<String, String> properties) {}
    
}
