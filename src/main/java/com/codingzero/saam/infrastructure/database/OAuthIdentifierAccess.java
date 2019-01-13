package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.OAuthIdentifierKey;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface OAuthIdentifierAccess extends TransactionalService {

    boolean isDuplicateKey(OAuthIdentifierKey key);

    int countByUserId(String applicationId, String userId);

    int countByPlatform(String applicationId, OAuthPlatform platform);

    void insert(OAuthIdentifierOS os);

    void update(OAuthIdentifierOS os);

    void delete(OAuthIdentifierOS os);

    void deleteByUserId(String applicationId, String userId);

    void deleteByPlatform(String applicationId, OAuthPlatform policyPlatform);

//    void deleteByPlatformAndUserId(String applicationId, OAuthPlatform platform, String userId);

    void deleteByApplicationId(String id);

    OAuthIdentifierOS selectByKey(OAuthIdentifierKey key);

    List<OAuthIdentifierOS> selectByUserId(String applicationId, String userId);

    PaginatedResult<List<OAuthIdentifierOS>> selectByPlatform(String applicationId, OAuthPlatform platform);

}
