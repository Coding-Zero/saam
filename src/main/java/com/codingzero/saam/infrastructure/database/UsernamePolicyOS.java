package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.UsernameFormat;

import java.util.Date;


public class UsernamePolicyOS extends IdentifierPolicyOS {

    private UsernameFormat format;

    public UsernamePolicyOS(String applicationId,
                            int minLength,
                            int maxLength,
                            boolean isActive,
                            Date creationTime,
                            Date updateTime,
                            UsernameFormat format) {
        super(applicationId, IdentifierType.USERNAME, false,
                minLength, maxLength, isActive, creationTime, updateTime);
        this.format = format;
    }

    public UsernameFormat getFormat() {
        return format;
    }
}
