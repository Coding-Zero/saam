package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface PrincipalAccess extends TransactionalService {

    String generateId(String applicationId, PrincipalType type);

    PrincipalOS selectById(String applicationId, String id);

    PaginatedResult<List<PrincipalOS>> selectByApplicationIdAndType(String applicationId, PrincipalType type);

    PaginatedResult<List<PrincipalOS>> selectByApplicationId(String applicationId);

}
