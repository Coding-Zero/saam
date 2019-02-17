package com.codingzero.saam.domain;

import java.util.Date;
import java.util.Map;

public interface UserSession {

    Application getApplication();

    String getKey();

    User getUser();

    Date getExpirationTime();

    Date getCreationTime();

    boolean isExpired();

    Map<String, Object> getDetails();

}
