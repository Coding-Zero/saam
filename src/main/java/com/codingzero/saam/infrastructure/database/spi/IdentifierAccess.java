package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface IdentifierAccess  extends TransactionalService {

    boolean isDuplicateContent(String applicationId, IdentifierType type, String content);

    void insert(IdentifierOS os);

    void update(IdentifierOS os);

    void delete(IdentifierOS os);

    void deleteByType(String applicationId, IdentifierType type);

    void deleteByTypeAndUserId(String applicationId, IdentifierType type, String userId);

    void deleteByApplicationId(String id);

    IdentifierOS selectByTypeAndContent(String applicationId, IdentifierType type, String content);

    List<IdentifierOS> selectByTypeAndUserId(String applicationId, IdentifierType type, String userId);

    PaginatedResult<List<IdentifierOS>> selectByPolicyCode(String applicationId, IdentifierType type);

}
