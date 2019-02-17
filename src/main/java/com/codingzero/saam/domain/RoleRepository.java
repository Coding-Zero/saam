package com.codingzero.saam.domain;

import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface RoleRepository {

    Role store(Role role);

    void remove(Role role);

    void removeByApplication(Application application);

    Role findById(Application application, String id);

    Role findByName(Application application, String name);

    PaginatedResult<List<Role>> findAll(Application application);

}
