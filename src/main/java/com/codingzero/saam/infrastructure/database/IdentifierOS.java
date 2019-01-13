package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierVerificationCode;

import java.util.Date;


public class IdentifierOS {

    private IdentifierKey id;
    private String userId;
    private boolean isVerified;
    private IdentifierVerificationCode verificationCode;
    private long creationTime;
    private long updateTime;

    public IdentifierOS(IdentifierKey id, String userId, boolean isVerified,
                        IdentifierVerificationCode verificationCode, Date creationTime,
                        Date updateTime) {
        this.id = id;
        this.userId = userId;
        this.isVerified = isVerified;
        this.verificationCode = verificationCode;
        this.creationTime = creationTime.getTime();
        setUpdateTime(updateTime);
    }

    public IdentifierKey getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public IdentifierVerificationCode getVerificationCode() {
        return verificationCode;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setVerificationCode(IdentifierVerificationCode verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Date getUpdateTime() {
        return new Date(updateTime);
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime.getTime();
    }

}
