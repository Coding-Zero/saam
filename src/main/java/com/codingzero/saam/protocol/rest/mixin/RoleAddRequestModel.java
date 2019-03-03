package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class RoleAddRequestModel {

    public RoleAddRequestModel(@JsonProperty("applicationId") String applicationId,
                               @JsonProperty("name") String name) {}
    
}
