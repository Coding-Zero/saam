package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface IdentifierAccess  extends TransactionalService {

    boolean isDuplicateContent(String applicationId, String policyCode, String content);

    void insert(IdentifierOS os);

    void update(IdentifierOS os);

    void delete(IdentifierOS os);

    void deleteByPolicyCode(String applicationId, String policyCode);

    void deleteByPolicyCodeAndUserId(String applicationId, String policyCode, String userId);

    void deleteByApplicationId(String id);

    IdentifierOS selectByPolicyCodeAndContent(String applicationId, String policyCode, String content);

    List<IdentifierOS> selectByPolicyCodeAndUserId(String applicationId, String policyCode, String userId);

    PaginatedResult<List<IdentifierOS>> selectByPolicyCode(String applicationId, String policyCode);

}
