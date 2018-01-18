package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.infrastructure.database.ApplicationOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface ApplicationAccess extends TransactionalService {

    boolean isDuplicateName(String name);

    String generateId();

    void insert(ApplicationOS os);

    void update(ApplicationOS os);

    void delete(ApplicationOS os);

    ApplicationOS selectById(String id);

    PaginatedResult<List<ApplicationOS>> selectAll();

}
