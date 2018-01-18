package com.codingzero.saam.core;

import java.util.Date;
import java.util.Map;

public interface OAuthIdentifier {

    User getUser();

    OAuthIdentifierPolicy getPolicy();

    String getContent();

    Map<String, Object> getProperties();

    void setProperties(Map<String, Object> properties);

    Date getCreationTime();

    Date getUpdateTime();

}
