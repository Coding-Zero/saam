package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface IdentifierPolicyAccess extends TransactionalService {

    boolean isDuplicateType(String applicationId, IdentifierType type);

    void insert(IdentifierPolicyOS os);

    void update(IdentifierPolicyOS os);

    void delete(IdentifierPolicyOS os);

    void deleteByApplicationId(String applicationId);

    IdentifierPolicyOS selectByType(String applicationId, IdentifierType type);

    List<IdentifierPolicyOS> selectByApplicationId(String applicationId);

}
