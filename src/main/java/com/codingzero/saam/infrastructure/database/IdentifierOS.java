package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.IdentifierVerificationCode;

import java.util.Date;


public class IdentifierOS {

    private String applicationId;
    private String identifierPolicyCode;
    private String content;
    private String userId;
    private IdentifierType type;
    private boolean isVerified;
    private IdentifierVerificationCode verificationCode;
    private long creationTime;
    private long updateTime;

    public IdentifierOS(String applicationId, String identifierPolicyCode, String content,
                        String userId, IdentifierType type, boolean isVerified,
                        IdentifierVerificationCode verificationCode, Date creationTime,
                        Date updateTime) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.identifierPolicyCode = identifierPolicyCode;
        this.content = content;
        this.type = type;
        this.isVerified = isVerified;
        this.verificationCode = verificationCode;
        this.creationTime = creationTime.getTime();
        setUpdateTime(updateTime);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getIdentifierPolicyCode() {
        return identifierPolicyCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public IdentifierType getType() {
        return type;
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
