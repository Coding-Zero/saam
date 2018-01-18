package com.codingzero.saam.common.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public abstract class PasswordResetCodeModel {

    public PasswordResetCodeModel(@JsonProperty("code") String code,
                                  @JsonProperty("expirationTime") Date expirationTime) {}
    
}
