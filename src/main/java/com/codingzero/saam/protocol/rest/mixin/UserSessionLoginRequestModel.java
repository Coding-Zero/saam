package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public abstract class UserSessionLoginRequestModel {

    public UserSessionLoginRequestModel(@JsonProperty("applicationId") String applicationId,
                                        @JsonProperty("sessionKey") String sessionKey,
                                        @JsonProperty("details") Map<String, String> details,
                                        @JsonProperty("extraTimeout") long extraTimeout) {}
    
}
