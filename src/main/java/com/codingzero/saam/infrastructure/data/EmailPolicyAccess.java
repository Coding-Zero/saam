package com.codingzero.saam.infrastructure.data;

import com.codingzero.utilities.transaction.TransactionalService;

public interface EmailPolicyAccess extends TransactionalService {

    void insert(EmailPolicyOS os);

    void update(EmailPolicyOS os);

    void delete(EmailPolicyOS os);

    void deleteByApplicationId(String applicationId);

    EmailPolicyOS selectByApplicationId(String applicationId);

    EmailPolicyOS selectByIdentifierPolicyOS(IdentifierPolicyOS os);

}
