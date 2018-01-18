package com.codingzero.saam.core;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OAuthIdentifierPolicy {

    Application getApplication();

    OAuthPlatform getPlatform();

    Map<String, Object> getConfigurations();

    void setConfigurations(Map<String, Object> configurations);

    boolean isActive();

    void setActive(boolean isActive);

    Date getCreationTime();

    Date getUpdateTime();

    /**SSOIdentifier**/

    OAuthIdentifier addIdentifier(String content, Map<String, Object> properties, User user);

    void updateIdentifier(OAuthIdentifier identifier);

    void removeIdentifier(OAuthIdentifier identifier);

    OAuthIdentifier fetchIdentifierById(String content);

    OAuthIdentifier fetchIdentifierByUserAndId(User user, String content);

//    OAuthIdentifier fetchDefaultIdentifier(User user);

    List<OAuthIdentifier> fetchIdentifiersByUser(User user);

    PaginatedResult<List<OAuthIdentifier>> fetchAllIdentifiers();

}
