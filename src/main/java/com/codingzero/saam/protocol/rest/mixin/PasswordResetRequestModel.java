package com.codingzero.saam.protocol.rest.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PasswordResetRequestModel {

    public PasswordResetRequestModel(@JsonProperty("applicationId") String applicationId,
                                     @JsonProperty("userId") String userId,
                                     @JsonProperty("resetCode") String resetCode,
                                     @JsonProperty("newPassword") String newPassword) {}
    
}
