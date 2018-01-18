package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class UserRoleUpdateRequestModel {

    public UserRoleUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                      @JsonProperty("userId") String userId,
                                      @JsonProperty("roleIds") List<String> roleIds) {}
    
}
