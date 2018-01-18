package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class APIKeyAddRequestModel {

    public APIKeyAddRequestModel(@JsonProperty("applicationId") String applicationId,
                                 @JsonProperty("userId") String userId,
                                 @JsonProperty("name") String name) {}
    
}
