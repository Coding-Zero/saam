package com.codingzero.saam.core;

import com.codingzero.saam.common.IdentifierVerificationCode;

import java.util.Date;

public interface Identifier {

    Application getApplication();

    IdentifierPolicy getPolicy();

    String getContent();

    User getUser();

    boolean isVerified();

    IdentifierVerificationCode generateVerificationCode(long timeout);

    IdentifierVerificationCode getVerificationCode();

    void verify(String code);

    Date getCreationTime();

    Date getUpdateTime();

}