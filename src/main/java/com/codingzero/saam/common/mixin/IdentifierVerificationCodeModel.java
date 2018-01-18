package com.codingzero.saam.common.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public abstract class IdentifierVerificationCodeModel {

    public IdentifierVerificationCodeModel(@JsonProperty("code") String code,
                                           @JsonProperty("expirationTime") Date expirationTime) {}
    
}
