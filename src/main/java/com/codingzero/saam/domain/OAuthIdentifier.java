package com.codingzero.saam.domain;

import com.codingzero.saam.common.OAuthPlatform;

import java.util.Date;
import java.util.Map;

public interface OAuthIdentifier {

    Application getApplication();

    OAuthPlatform getPlatform();

    String getContent();

    User getUser();

    Map<String, Object> getProperties();

    void setProperties(Map<String, Object> properties);

    Date getCreationTime();

    Date getUpdateTime();

}
