package com.codingzero.saam.infrastructure.data;

import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface ResourceAccess extends TransactionalService {

    boolean isDuplicateKey(String applicationId, String key);

    void insert(ResourceOS os);

    void update(ResourceOS os);

    void delete(ResourceOS os);

    void deleteByApplicationId(String id);

    ResourceOS selectByKey(String applicationId, String key);

    PaginatedResult<List<ResourceOS>> selectByPrincipalId(
            String applicationId, String parentKey, String principalId);

    PaginatedResult<List<ResourceOS>> selectAll(String applicationId, String parentKey);

}
