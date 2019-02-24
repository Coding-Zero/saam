package com.codingzero.saam.domain;

import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface UserSessionRepository {

    UserSession store(UserSession session);

    void remove(UserSession entity);

    void removeByUser(User user);

    void removeByApplication(Application application);

    UserSession findByKey(Application application, String key);

    PaginatedResult<List<UserSession>> findByOwner(User user);

}
