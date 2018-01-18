package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.UserOS;
import com.codingzero.utilities.transaction.TransactionalService;

public interface UserAccess extends TransactionalService {

    void insert(UserOS os);

    void update(UserOS os);

    void delete(UserOS os);

    void deleteByApplicationId(String id);

//    UserOS selectById(String applicationId, String id);

    UserOS selectByPrincipalOS(PrincipalOS os);

//    PaginatedResult<List<UserOS>> selectByApplicationId(String id);

}
