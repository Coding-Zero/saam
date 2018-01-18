package com.codingzero.saam.presentation.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.codingzero.saam.common.PasswordPolicy;

public abstract class PasswordPolicyUpdateRequestModel {

    public PasswordPolicyUpdateRequestModel(@JsonProperty("applicationId") String applicationId,
                                            @JsonProperty("passwordPolicy") PasswordPolicy passwordPolicy) {}
    
}
