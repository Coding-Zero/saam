package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.infrastructure.database.UserSessionOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface UserSessionAccess extends TransactionalService {

    String generateKey(String applicationId);

    void insert(UserSessionOS os);

    void delete(UserSessionOS os);

    void deleteByUserId(String applicationId, String userId);

    void deleteByApplicationId(String id);

    UserSessionOS selectByKey(String applicationId, String key);

    PaginatedResult<List<UserSessionOS>> selectByUserId(String applicationId, String userId);

}
