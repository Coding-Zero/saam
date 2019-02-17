package com.codingzero.saam.domain;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface OAuthIdentifierRepository {

    OAuthIdentifier store(OAuthIdentifier identifier);

    void remove(OAuthIdentifier identifier);

    void removeByPlatform(Application application, OAuthPlatform platform);

    void removeByUser(User user);

    void removeByApplication(Application application);

    OAuthIdentifier findByKey(OAuthIdentifierPolicy policy, String content);

    List<OAuthIdentifier> findByUser(Application application, User user);

    PaginatedResult<List<OAuthIdentifier>> findByPolicy(OAuthIdentifierPolicy policy);

}
