package com.codingzero.saam.domain.principal.user;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.UserRepository;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.infrastructure.data.IdentifierAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.PrincipalOS;
import com.codingzero.saam.infrastructure.data.UserAccess;
import com.codingzero.saam.infrastructure.data.UserOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultFetchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRepositoryService implements UserRepository {

    private UserAccess access;
    private PrincipalAccess principalAccess;
    private IdentifierAccess identifierAccess;
    private OAuthIdentifierAccess oAuthIdentifierAccess;
    private UserFactoryService factory;
    private ApplicationStatusVerifier applicationStatusVerifier;

    public UserRepositoryService(UserAccess access,
                                 PrincipalAccess principalAccess,
                                 IdentifierAccess identifierAccess,
                                 OAuthIdentifierAccess oAuthIdentifierAccess,
                                 UserFactoryService factory,
                                 ApplicationStatusVerifier applicationStatusVerifier) {
        this.access = access;
        this.principalAccess = principalAccess;
        this.identifierAccess = identifierAccess;
        this.oAuthIdentifierAccess = oAuthIdentifierAccess;
        this.factory = factory;
        this.applicationStatusVerifier = applicationStatusVerifier;
    }

    @Override
    public User store(User user) {
        applicationStatusVerifier.checkForDeactiveStatus(user.getApplication());
        UserEntity entity = (UserEntity) user;
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        return factory.reconstitute(entity.getObjectSegment(), user.getApplication());
    }

    @Override
    public void remove(User user) {
        applicationStatusVerifier.checkForDeactiveStatus(user.getApplication());
        checkForUnremoveableStatus(user);
        UserEntity entity = (UserEntity) user;
        access.delete(entity.getObjectSegment());
    }

    private void checkForUnremoveableStatus(User user) {
        if (identifierAccess.countByUserId(user.getApplication().getId(), user.getId()) > 0) {
            throw new IllegalStateException(
                    "User " + user.getId() + " cannot be removed before removing existing identifiers.");
        }
        if (oAuthIdentifierAccess.countByUserId(user.getApplication().getId(), user.getId()) > 0) {
            throw new IllegalStateException(
                    "User " + user.getId() + " cannot be removed before removing existing oauth identifiers.");
        }
    }

    @Override
    public void removeByApplication(Application application) {
        access.deleteByApplicationId(application.getId());
    }

    @Override
    public UserEntity findById(Application application, String id) {
        PrincipalOS principalOS = principalAccess.selectById(application.getId(), id);
        return load(application, principalOS);
    }

    public UserEntity load(Application application, PrincipalOS principalOS) {
        if (null == principalOS) {
            return null;
        }
        UserOS os = access.selectByPrincipalOS(principalOS);
        return factory.reconstitute(os, application);
    }

    @Override
    public PaginatedResult<List<User>> findByApplication(Application application) {
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
