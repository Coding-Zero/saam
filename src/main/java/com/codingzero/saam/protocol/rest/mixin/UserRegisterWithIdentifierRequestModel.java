package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public abstract class UserRegisterWithIdentifierRequestModel {

    public UserRegisterWithIdentifierRequestModel(@JsonProperty("applicationId") String applicationId,
                                                  @JsonProperty("identifiers") Map<String, String> identifiers,
                                                  @JsonProperty("password") String password,
                                                  @JsonProperty("roleIds") List<String> roleIds
                                                  ) {}
    
}
