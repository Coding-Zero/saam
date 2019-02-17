package com.codingzero.saam.domain;

import com.codingzero.saam.common.PrincipalType;

import java.util.Date;

public interface Principal {

    Application getApplication();

    String getId();

    PrincipalType getType();

    Date getCreationTime();

}
