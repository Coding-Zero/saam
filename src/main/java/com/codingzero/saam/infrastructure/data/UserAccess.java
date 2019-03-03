package com.codingzero.saam.infrastructure.data;

import com.codingzero.utilities.transaction.TransactionalService;

public interface UserAccess extends TransactionalService {

    void insert(UserOS os);

    void update(UserOS os);

    void delete(UserOS os);

    void deleteByApplicationId(String id);

    UserOS selectByPrincipalOS(PrincipalOS os);

}
