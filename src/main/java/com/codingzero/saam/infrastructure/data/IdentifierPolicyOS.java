package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.common.IdentifierType;

import java.util.Date;

public class IdentifierPolicyOS {

    private String applicationId;
    private IdentifierType type;
    private boolean verificationRequired;
    private int minLength;
    private int maxLength;
    private boolean isActive;
    private long creationTime;
    private long updateTime;

    public IdentifierPolicyOS(String applicationId,
                              IdentifierType type,
                              boolean verificationRequired,
                              int minLength,
                              int maxLength,
                              boolean isActive,
                              Date creationTime,
                              Date updateTime) {
        this.applicationId = applicationId;
        this.type = type;
        this.verificationRequired = verificationRequired;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.isActive = isActive;
        this.creationTime = creationTime.getTime();
        setUpdateTime(updateTime);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public IdentifierType getType() {
        return type;
    }

    public boolean isVerificationRequired() {
        return verificationRequired;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public boolean isActive() {
        return isActive;
    }

    public Date getCreationTime() {
        return new Date(creationTime);
    }

    public Date getUpdateTime() {
        return new Date(updateTime);
    }

    public void setVerificationRequired(boolean verificationRequired) {
        this.verificationRequired = verificationRequired;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime.getTime();
    }
}
