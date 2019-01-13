package com.codingzero.saam.infrastructure.database;

import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface PermissionResourceIndexAccess {

    PaginatedResult<List<PermissionResourceIndex>> selectByPrincipalId(String applicationId, String principalId);
}
