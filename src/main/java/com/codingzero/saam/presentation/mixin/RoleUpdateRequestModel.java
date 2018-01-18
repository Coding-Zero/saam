package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class RoleUpdateRequestModel {

    public RoleUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                  @JsonProperty("id") String id,
                                  @JsonProperty("name") String name) {}
    
}
