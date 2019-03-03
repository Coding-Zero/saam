package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class UserRoleUpdateRequestModel {

    public UserRoleUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                      @JsonProperty("userId") String userId,
                                      @JsonProperty("roleIds") List<String> roleIds) {}
    
}
