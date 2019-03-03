package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public abstract class UserSessionCreateRequestModel {

    public UserSessionCreateRequestModel(@JsonProperty("applicationId") String applicationId,
                                         @JsonProperty("userId") String userId,
                                         @JsonProperty("details") Map<String, String> details,
                                         @JsonProperty("timeout") long timeout) {}
    
}
