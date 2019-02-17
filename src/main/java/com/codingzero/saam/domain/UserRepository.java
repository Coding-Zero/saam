package com.codingzero.saam.domain;

import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface UserRepository {

    void store(User user);

    void remove(User user);

    void removeByApplication(Application application);

    User findById(Application application, String id);

    PaginatedResult<List<User>> findByApplication(Application application);

}
