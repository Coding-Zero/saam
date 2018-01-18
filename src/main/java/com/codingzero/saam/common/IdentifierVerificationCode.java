package com.codingzero.saam.common;

import java.util.Date;


public class IdentifierVerificationCode {

    private String code;
    private long expirationTime;

    public IdentifierVerificationCode(String code, Date expirationTime) {
        this.code = code;
        this.expirationTime = expirationTime.getTime();
    }

    public String getCode() {
        return code;
    }

    public Date getExpirationTime() {
        return new Date(expirationTime);
    }
}
