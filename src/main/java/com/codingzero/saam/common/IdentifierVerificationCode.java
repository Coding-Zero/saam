package com.codingzero.saam.common;

import java.util.Date;
import java.util.Objects;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierVerificationCode that = (IdentifierVerificationCode) o;
        return getExpirationTime().equals(that.getExpirationTime()) &&
                Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getExpirationTime());
    }
}
