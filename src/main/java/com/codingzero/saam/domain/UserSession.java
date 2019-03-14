package com.codingzero.saam.domain;

import java.util.Date;
import java.util.Map;

public interface UserSession {

    Application getApplication();

    String getKey();

    User getUser();

    Date getExpirationTime();

    void expendExpirationTime(long millionSeconds);

    Date getCreationTime();

    boolean isExpired();

    Map<String, Object> getDetails();

    void setDetails(Map<String, Object> details);

}
