package com.codingzero.saam.core;

import com.codingzero.saam.common.IdentifierVerificationCode;

import java.util.Date;

public interface Identifier {

    User getUser();

    IdentifierPolicy getPolicy();

    String getContent();

    boolean isVerified();

    IdentifierVerificationCode generateVerificationCode(long timeout);

    IdentifierVerificationCode getVerificationCode();

    void verify(String code);

    Date getCreationTime();

    Date getUpdateTime();

}