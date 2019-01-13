package com.codingzero.saam.core;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface IdentifierRepository {

    Identifier store(Identifier identifier);

    void remove(Identifier identifier);

    void removeByType(Application application, IdentifierType type);

    void removeByUser(User user);

    void removeByApplication(Application application);

    Identifier findById(Application application, IdentifierType type, String content);

    List<Identifier> findByUser(Application application, User user);

    PaginatedResult<List<Identifier>> findByIdentifierType(Application application, IdentifierType type);

    PaginatedResult<List<Identifier>> findByApplication(Application application);

}
