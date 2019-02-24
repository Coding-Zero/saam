package com.codingzero.saam.domain;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface IdentifierRepository {

    Identifier store(Identifier identifier);

    void remove(Identifier identifier);

    void removeByType(Application application, IdentifierType type);

    void removeByUser(User user);

    void removeByApplication(Application application);

    Identifier findByKey(Application application, String content);

    List<Identifier> findByUser(Application application, User user);

    PaginatedResult<List<Identifier>> findByIdentifierType(Application application, IdentifierType type);

    PaginatedResult<List<Identifier>> findByApplication(Application application);

}
