package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyOS;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface OAuthIdentifierPolicyAccess extends TransactionalService {

    boolean isDuplicatePlatform(String applicationId, OAuthPlatform platform);

    void insert(OAuthIdentifierPolicyOS os);

    void update(OAuthIdentifierPolicyOS os);

    void delete(OAuthIdentifierPolicyOS os);

    void deleteByApplicationId(String id);

    OAuthIdentifierPolicyOS selectByPlatform(String applicationId, OAuthPlatform platform);

    List<OAuthIdentifierPolicyOS> selectByApplicationId(String applicationId);

}
