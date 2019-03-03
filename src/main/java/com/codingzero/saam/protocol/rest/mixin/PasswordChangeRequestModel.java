package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PasswordChangeRequestModel {

    public PasswordChangeRequestModel(@JsonProperty("applicationId") String applicationId,
                                      @JsonProperty("userId") String userId,
                                      @JsonProperty("oldPassword") String oldPassword,
                                      @JsonProperty("newPassword") String newPassword) {}
    
}
