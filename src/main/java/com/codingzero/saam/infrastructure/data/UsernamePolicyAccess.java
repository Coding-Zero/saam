package com.codingzero.saam.infrastructure.data;

import com.codingzero.utilities.transaction.TransactionalService;

public interface UsernamePolicyAccess extends TransactionalService {

    void insert(UsernamePolicyOS os);

    void update(UsernamePolicyOS os);

    void delete(UsernamePolicyOS os);

    void deleteByApplicationId(String id);

    UsernamePolicyOS selectByApplicationId(String applicationId);

    UsernamePolicyOS selectByIdentifierPolicyOS(IdentifierPolicyOS os);

}
