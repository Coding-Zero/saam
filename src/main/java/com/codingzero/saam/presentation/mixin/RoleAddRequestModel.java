package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class RoleAddRequestModel {

    public RoleAddRequestModel(@JsonProperty("applicationId") String applicationId,
                               @JsonProperty("name") String name) {}
    
}
