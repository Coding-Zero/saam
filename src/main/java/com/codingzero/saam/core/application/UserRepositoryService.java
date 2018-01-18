package com.codingzero.saam.core.application;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.User;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.saam.infrastructure.database.UserOS;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.spi.UserAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRepositoryService {

    private UserAccess access;
    private PrincipalAccess principalAccess;
    private UserFactoryService factory;

    public UserRepositoryService(UserAccess access,
                                 PrincipalAccess principalAccess,
                                 UserFactoryService factory) {
        this.access = access;
        this.principalAccess = principalAccess;
        this.factory = factory;
    }

    public void store(UserEntity entity) {
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
    }

    public void remove(UserEntity entity) {
        access.delete(entity.getObjectSegment());
    }

    public void removeAll(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    public UserEntity findById(Application application, String id) {
        PrincipalOS principalOS = principalAccess.selectById(application.getId(), id);
        return load(application, principalOS);
    }

    public UserEntity load(Application application, PrincipalOS principalOS) {
        UserOS os = access.selectByPrincipalOS(principalOS);
        return factory.reconstitute(os, application);
    }

    public PaginatedResult<List<User>> findAll(Application application) {
        return new PaginatedResult<>(request -> _findAll(request), application);
    }

    private List<User> _findAll(ResultFetchRequest request) {
        Application application = (Application) request.getArguments()[0];
        PaginatedResult<List<PrincipalOS>> result =
                principalAccess.selectByApplicationIdAndType(application.getId(), PrincipalType.USER);
        result = result.start(request.getPage(), request.getSorting());
        List<PrincipalOS> osList = result.getResult();
        List<User> entities = new ArrayList<>(osList.size());
        for (PrincipalOS os: osList) {
            entities.add(load(application, os));
        }
        return Collections.unmodifiableList(entities);
    }
}
