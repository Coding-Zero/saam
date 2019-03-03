package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class APIKeyAddRequestModel {

    public APIKeyAddRequestModel(@JsonProperty("applicationId") String applicationId,
                                 @JsonProperty("userId") String userId,
                                 @JsonProperty("name") String name) {}
    
}
