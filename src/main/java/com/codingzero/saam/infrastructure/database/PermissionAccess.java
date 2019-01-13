package com.codingzero.saam.infrastructure.database;

import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface PermissionAccess extends TransactionalService {

    boolean isDuplicate(String applicationId, String resourceKey, String principalId);

    void insert(PermissionOS os);

    void update(PermissionOS os);

    void delete(PermissionOS os);

    void deleteByPrincipalId(String applicationId, String principalId);

    void deleteByResourceKey(String applicationId, String resourceKey);

    void deleteByApplicationId(String applicationId);

    PermissionOS selectByResourceKeyAndPrincipalId(String applicationId, String resourceKey, String principalId);

    PaginatedResult<List<PermissionOS>> selectByResourceKey(String applicationId, String resourceKey);

    PaginatedResult<List<PermissionOS>> selectByPrincipalId(String applicationId, String principalId);

}
