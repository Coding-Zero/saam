package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.UsernameFormat;

public abstract class UsernamePolicyUpdateRequestModel {

    public UsernamePolicyUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                            @JsonProperty("code") String code,
                                            @JsonProperty("active") boolean isActive) {}
    
}
