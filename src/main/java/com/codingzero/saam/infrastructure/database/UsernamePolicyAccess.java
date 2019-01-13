package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.UsernamePolicyOS;
import com.codingzero.utilities.transaction.TransactionalService;

public interface UsernamePolicyAccess extends TransactionalService {

    void insert(UsernamePolicyOS os);

    void update(UsernamePolicyOS os);

    void delete(UsernamePolicyOS os);

    void deleteByApplicationId(String id);

    UsernamePolicyOS selectByApplicationId(String applicationId);

    UsernamePolicyOS selectByIdentifierPolicyOS(IdentifierPolicyOS os);

}
