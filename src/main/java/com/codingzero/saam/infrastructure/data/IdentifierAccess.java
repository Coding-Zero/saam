package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface IdentifierAccess  extends TransactionalService {

    boolean isDuplicateContent(String applicationId, String content);

    int countByUserId(String applicationId, String userId);

    int countByType(String applicationId, IdentifierType type);

    void insert(IdentifierOS os);

    void update(IdentifierOS os);

    void delete(IdentifierOS os);

    void deleteByType(String applicationId, IdentifierType type);

    void deleteByUserId(String applicationId, String userId);

    void deleteByTypeAndUserId(String applicationId, IdentifierType type, String userId);

    void deleteByApplicationId(String id);

    IdentifierOS selectByKey(IdentifierKey key);

    List<IdentifierOS> selectByUserId(String applicationId, String userId);

    PaginatedResult<List<IdentifierOS>> selectByType(String applicationId, IdentifierType type);

    PaginatedResult<List<IdentifierOS>> selectByApplicationId(String applicationId);

}
