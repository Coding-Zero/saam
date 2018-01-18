package com.codingzero.saam.common.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PasswordPolicyModel {

    public PasswordPolicyModel(@JsonProperty("minLength") int source,
                               @JsonProperty("maxLength") int maxLength,
                               @JsonProperty("needCapital") boolean isNeedCapital,
                               @JsonProperty("needSpecialChar") boolean isNeedSpecialChar) {}
    
}
