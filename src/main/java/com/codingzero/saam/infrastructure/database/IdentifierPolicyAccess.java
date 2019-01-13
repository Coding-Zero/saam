package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface IdentifierPolicyAccess extends TransactionalService {

    boolean isDuplicateType(String applicationId, IdentifierType type);

    IdentifierPolicyOS selectByType(String applicationId, IdentifierType type);

    List<IdentifierPolicyOS> selectByApplicationId(String applicationId);

}
