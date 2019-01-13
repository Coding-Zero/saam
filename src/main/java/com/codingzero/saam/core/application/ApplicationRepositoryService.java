package com.codingzero.saam.core.application;

import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.ApplicationRepository;
import com.codingzero.saam.core.principal.PrincipalEntity;
import com.codingzero.saam.core.principal.PrincipalRepositoryService;
import com.codingzero.saam.core.resource.PermissionRepositoryService;
import com.codingzero.saam.core.resource.ResourceEntity;
import com.codingzero.saam.core.resource.ResourceRepositoryService;
import com.codingzero.saam.core.usersession.UserSessionEntity;
import com.codingzero.saam.core.usersession.UserSessionRepositoryService;
import com.codingzero.saam.core.principal.user.UserEntity;
import com.codingzero.saam.infrastructure.database.ApplicationOS;
import com.codingzero.saam.infrastructure.database.ApplicationAccess;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationRepositoryService implements ApplicationRepository {

    private ApplicationAccess access;
    private ApplicationFactoryService factory;
    private IdentifierPolicyRepositoryService identifierPolicyRepository;
    private OAuthIdentifierPolicyRepositoryService oAuthIdentifierPolicyRepository;
    private PrincipalRepositoryService principalRepository;
    private ResourceRepositoryService resourceRepository;
    private PermissionRepositoryService permissionRepository;
    private UserSessionRepositoryService userSessionRepository;

    public ApplicationRepositoryService(ApplicationAccess access,
                                        ApplicationFactoryService factory,
                                        IdentifierPolicyRepositoryService identifierPolicyRepository,
                                        OAuthIdentifierPolicyRepositoryService oAuthIdentifierPolicyRepository,
                                        PrincipalRepositoryService principalRepository,
                                        ResourceRepositoryService resourceRepository,
                                        PermissionRepositoryService permissionRepository,
                                        UserSessionRepositoryService userSessionRepository) {
        this.access = access;
        this.factory = factory;
        this.identifierPolicyRepository = identifierPolicyRepository;
        this.oAuthIdentifierPolicyRepository = oAuthIdentifierPolicyRepository;
        this.principalRepository = principalRepository;
        this.resourceRepository = resourceRepository;
        this.permissionRepository = permissionRepository;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public Application store(Application application) {
        ApplicationRoot entity = (ApplicationRoot) application;
        if (entity.isNew()) {
            access.insert(entity.getObjectSegment());
        } else if (entity.isDirty()) {
            access.update(entity.getObjectSegment());
        }
        flushDirtyIdentifierPolicies(entity);
        flushDirtyOAuthIdentifierPolicies(entity);
        flushDirtyPrincipals(entity);
        flushDirtyUserSessions(entity);
        flushRemovingUserSessions(entity);
        flushDirtyResources(entity);
        return factory.reconstitute(entity.getObjectSegment());
    }

    private void flushDirtyIdentifierPolicies(ApplicationRoot application) {
        List<IdentifierPolicyEntity> entities = application.getDirtyIdentifierPolicies();
        for (IdentifierPolicyEntity entity: entities) {
            if (entity.isVoid()) {
                identifierPolicyRepository.remove(entity);
            } else {
                identifierPolicyRepository.store(entity);
            }
        }
    }

    private void flushDirtyOAuthIdentifierPolicies(ApplicationRoot application) {
        List<OAuthIdentifierPolicyEntity> entities = application.getDirtyOAuthIdentifierPolicies();
        for (OAuthIdentifierPolicyEntity entity: entities) {
            if (entity.isVoid()) {
                oAuthIdentifierPolicyRepository.remove(entity);
            } else {
                oAuthIdentifierPolicyRepository.store(entity);
            }
        }
    }

    private void flushDirtyPrincipals(ApplicationRoot application) {
        List<PrincipalEntity> entities = application.getDirtyPrincipals();
        for (PrincipalEntity entity: entities) {
            if (entity.isVoid()) {
                principalRepository.remove(entity);
                permissionRepository.remove(entity);
            } else {
                principalRepository.store(entity);
            }
        }
    }

    private void flushDirtyUserSessions(ApplicationRoot application) {
        List<UserSessionEntity> entities = application.getDirtyUserSessions();
        for (UserSessionEntity entity: entities) {
            if (entity.isVoid()) {
                userSessionRepository.remove(entity);
            } else {
                userSessionRepository.store(entity);
            }
        }
    }

    private void flushRemovingUserSessions(ApplicationRoot application) {
        List<UserEntity> entities = application.getRemovingUserSessions();
        for (UserEntity entity: entities) {
            userSessionRepository.removeByUser(entity);
        }
    }

    private void flushDirtyResources(ApplicationRoot application) {
        List<ResourceEntity> entities = application.getDirtyResources();
        for (ResourceEntity entity: entities) {
            if (entity.isVoid()) {
                resourceRepository.remove(entity);
                permissionRepository.remove(entity);
            } else {
                resourceRepository.store(entity);
            }
        }
    }

    @Override
    public void remove(Application application) {
        ApplicationRoot entity = (ApplicationRoot) application;
        identifierPolicyRepository.removeAll(entity);
        oAuthIdentifierPolicyRepository.removeAll(entity);
        userSessionRepository.removeByApplication(entity);
        resourceRepository.removeByApplication(entity);
        principalRepository.removeAll(entity);
        access.delete(entity.getObjectSegment());
    }

    @Override
    public Application findById(String id) {
        ApplicationOS os = access.selectById(id);
        return factory.reconstitute(os);
    }

    @Override
    public PaginatedResult<List<Application>> findAll() {
        PaginatedResult<List<ApplicationOS>> result = access.selectAll();
        return new PaginatedResult<>(new PaginatedResultMapper<List<Application>, List<ApplicationOS>>() {
            @Override
            protected List<Application> toResult(List<ApplicationOS> source, Object[] arguments) {
                return _toResult(source);
            }
        }, result);
    }

    private List<Application> _toResult(List<ApplicationOS> source) {
        List<Application> entities = new ArrayList<>(source.size());
        for (ApplicationOS os: source) {
            entities.add(factory.reconstitute(os));
        }
        return Collections.unmodifiableList(entities);
    }
}
