package com.codingzero.saam.domain;

import java.util.Date;
import java.util.Map;

public interface OAuthIdentifier {

    Application getApplication();

    OAuthIdentifierPolicy getPolicy();

    String getContent();

    User getUser();

    Map<String, Object> getProperties();

    void setProperties(Map<String, Object> properties);

    Date getCreationTime();

    Date getUpdateTime();

}
