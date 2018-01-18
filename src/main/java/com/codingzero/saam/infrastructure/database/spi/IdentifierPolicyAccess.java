package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface IdentifierPolicyAccess extends TransactionalService {

    boolean isDuplicateCode(String applicationId, String code);

    void insert(IdentifierPolicyOS os);

    void update(IdentifierPolicyOS os);

    void delete(IdentifierPolicyOS os);

    void deleteByApplicationId(String applicationId);

    IdentifierPolicyOS selectByCode(String applicationId, String code);

    List<IdentifierPolicyOS> selectByApplicationIdAndType(String applicationId, IdentifierType type);

    List<IdentifierPolicyOS> selectByApplicationId(String applicationId);

}
