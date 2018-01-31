package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface OAuthIdentifierAccess extends TransactionalService {

    boolean isDuplicateContent(String applicationId, OAuthPlatform policyPlatform, String content);

    void insert(OAuthIdentifierOS os);

    void update(OAuthIdentifierOS os);

    void delete(OAuthIdentifierOS os);

    void deleteByPlatform(String applicationId, OAuthPlatform policyPlatform);

    void deleteByPlatformAndUserId(String applicationId, OAuthPlatform platform, String userId);

    void deleteByApplicationId(String id);

    OAuthIdentifierOS selectByPlatformAndContent(String applicationId, OAuthPlatform platform, String content);

    List<OAuthIdentifierOS> selectByPlatformAndUserId(String applicationId, OAuthPlatform platform, String userId);

    PaginatedResult<List<OAuthIdentifierOS>> selectByPlatform(String applicationId, OAuthPlatform platform);

}
