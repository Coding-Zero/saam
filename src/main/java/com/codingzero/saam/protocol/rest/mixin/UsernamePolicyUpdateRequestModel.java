package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class UsernamePolicyUpdateRequestModel {

    public UsernamePolicyUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                            @JsonProperty("active") boolean isActive) {}
    
}
