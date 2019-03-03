package com.codingzero.saam.infrastructure.data;

import com.codingzero.utilities.transaction.TransactionalService;

public interface RoleAccess extends TransactionalService {

    boolean isDuplicateName(String applicationId, String name);

    void insert(RoleOS os);

    void update(RoleOS os);

    void delete(RoleOS os);

    void deleteByApplicationId(String id);

    RoleOS selectByName(String applicationId, String name);

    RoleOS selectByPrincipalOS(PrincipalOS os);

}
