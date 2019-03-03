package com.codingzero.saam.infrastructure.data;

import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface UserSessionAccess extends TransactionalService {

    String generateKey(String applicationId);

    int countByUserId(String applicationId, String userId);

    void insert(UserSessionOS os);

    void delete(UserSessionOS os);

    void deleteByUserId(String applicationId, String userId);

    void deleteByApplicationId(String id);

    UserSessionOS selectByKey(String applicationId, String key);

    PaginatedResult<List<UserSessionOS>> selectByUserId(String applicationId, String userId);

}
