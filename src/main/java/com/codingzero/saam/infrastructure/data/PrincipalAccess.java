package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface PrincipalAccess extends TransactionalService {

    String generateId(String applicationId, PrincipalType type);

    void insert(PrincipalOS os);

    void update(PrincipalOS os);

    void delete(PrincipalOS os);

    void deleteByApplicationIdAndType(String id, PrincipalType type);

    void deleteByApplicationId(String id);

    PrincipalOS selectById(String applicationId, String id);

    PaginatedResult<List<PrincipalOS>> selectByApplicationIdAndType(String applicationId, PrincipalType type);

    PaginatedResult<List<PrincipalOS>> selectByApplicationId(String applicationId);

}
