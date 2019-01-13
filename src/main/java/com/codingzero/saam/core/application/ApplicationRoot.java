package com.codingzero.saam.core.application;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.common.ResourceKeySeparator;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.EmailPolicy;
import com.codingzero.saam.core.IdentifierPolicy;
import com.codingzero.saam.core.OAuthIdentifierPolicy;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.core.Role;
import com.codingzero.saam.core.UsernamePolicy;
import com.codingzero.saam.core.principal.apikey.APIKeyFactoryService;
import com.codingzero.saam.core.principal.apikey.APIKeyRepositoryService;
import com.codingzero.saam.core.principal.PrincipalEntity;
import com.codingzero.saam.core.principal.PrincipalRepositoryService;
import com.codingzero.saam.core.resource.ResourceEntity;
import com.codingzero.saam.core.resource.ResourceFactoryService;
import com.codingzero.saam.core.resource.ResourceRepositoryService;
import com.codingzero.saam.core.principal.role.RoleEntity;
import com.codingzero.saam.core.principal.role.RoleFactoryService;
import com.codingzero.saam.core.principal.role.RoleRepositoryService;
import com.codingzero.saam.core.services.ApplicationStatusVerifier;
import com.codingzero.saam.core.usersession.UserSessionEntity;
import com.codingzero.saam.core.usersession.UserSessionFactoryService;
import com.codingzero.saam.core.usersession.UserSessionRepositoryService;
import com.codingzero.saam.core.principal.user.UserEntity;
import com.codingzero.saam.core.principal.user.UserFactoryService;
import com.codingzero.saam.core.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.ApplicationOS;
import com.codingzero.utilities.ddd.EntityObject;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationRoot extends EntityObject<ApplicationOS> implements Application {

    private ApplicationFactoryService factory;
    private IdentifierPolicyRepositoryService identifierPolicyRepository;
    private UsernamePolicyFactoryService usernameIdentifierPolicyFactory;
    private EmailPolicyFactoryService emailIdentifierPolicyFactory;
    private OAuthIdentifierPolicyFactoryService oAuthIdentifierPolicyFactory;
    private OAuthIdentifierPolicyRepositoryService oAuthIdentifierPolicyRepository;
    private PrincipalRepositoryService principalRepository;
    private UserFactoryService userFactory;
    private UserRepositoryService userRepository;
    private APIKeyFactoryService apiKeyFactory;
    private APIKeyRepositoryService apiKeyRepository;
    private UserSessionFactoryService userSessionFactory;
    private UserSessionRepositoryService userSessionRepository;
    private RoleFactoryService roleFactory;
    private RoleRepositoryService roleRepository;
    private ResourceFactoryService resourceFactory;
    private ResourceRepositoryService resourceRepository;
    private Map<IdentifierType, IdentifierPolicyEntity> dirtyIdentifierPolicies;
    private Map<OAuthPlatform, OAuthIdentifierPolicyEntity> dirtyOAuthIdentifierPolicies;
    private Map<String, PrincipalEntity> dirtyPrincipals;
    private Map<String, UserSessionEntity> dirtyUserSessions;
    private Map<String, UserEntity> removingUserSessions;
    private Map<String, ResourceEntity> dirtyResources;
    private ApplicationStatusVerifier statusVerifier;

    public ApplicationRoot(ApplicationOS objectSegment,
                           ApplicationFactoryService factory,
                           IdentifierPolicyRepositoryService identifierPolicyRepository,
                           UsernamePolicyFactoryService usernameIdentifierPolicyFactory,
                           EmailPolicyFactoryService emailIdentifierPolicyFactory,
                           OAuthIdentifierPolicyFactoryService oAuthIdentifierPolicyFactory,
                           OAuthIdentifierPolicyRepositoryService oAuthIdentifierPolicyRepository,
                           PrincipalRepositoryService principalRepository,
                           UserFactoryService userFactory,
                           UserRepositoryService userRepository,
                           APIKeyFactoryService apiKeyFactory,
                           APIKeyRepositoryService apiKeyRepository,
                           UserSessionFactoryService userSessionFactory,
                           UserSessionRepositoryService userSessionRepository,
                           RoleFactoryService roleFactory,
                           RoleRepositoryService roleRepository,
                           ResourceFactoryService resourceFactory,
                           ResourceRepositoryService resourceRepository, ApplicationStatusVerifier statusVerifier) {
        super(objectSegment);
        this.factory = factory;
        this.identifierPolicyRepository = identifierPolicyRepository;
        this.usernameIdentifierPolicyFactory = usernameIdentifierPolicyFactory;
        this.emailIdentifierPolicyFactory = emailIdentifierPolicyFactory;
        this.oAuthIdentifierPolicyFactory = oAuthIdentifierPolicyFactory;
        this.oAuthIdentifierPolicyRepository = oAuthIdentifierPolicyRepository;
        this.principalRepository = principalRepository;
        this.userFactory = userFactory;
        this.userRepository = userRepository;
        this.apiKeyFactory = apiKeyFactory;
        this.apiKeyRepository = apiKeyRepository;
        this.userSessionFactory = userSessionFactory;
        this.userSessionRepository = userSessionRepository;
        this.roleFactory = roleFactory;
        this.roleRepository = roleRepository;
        this.resourceFactory = resourceFactory;
        this.resourceRepository = resourceRepository;
        this.statusVerifier = statusVerifier;
        this.dirtyIdentifierPolicies = new HashMap<>();
        this.dirtyOAuthIdentifierPolicies = new HashMap<>();
        this.dirtyPrincipals = new HashMap<>();
        this.dirtyUserSessions = new HashMap<>();
        this.removingUserSessions = new HashMap<>();
        this.dirtyResources = new HashMap<>();
    }

    public List<IdentifierPolicyEntity> getDirtyIdentifierPolicies() {
        return Collections.unmodifiableList(new ArrayList<>(dirtyIdentifierPolicies.values()));
    }

    public List<OAuthIdentifierPolicyEntity> getDirtyOAuthIdentifierPolicies() {
        return Collections.unmodifiableList(new ArrayList<>(dirtyOAuthIdentifierPolicies.values()));
    }

    public List<PrincipalEntity> getDirtyPrincipals() {
        return Collections.unmodifiableList(new ArrayList<>(dirtyPrincipals.values()));
    }

    public List<UserSessionEntity> getDirtyUserSessions() {
        return Collections.unmodifiableList(new ArrayList<>(dirtyUserSessions.values()));
    }

    public List<UserEntity> getRemovingUserSessions() {
        return Collections.unmodifiableList(new ArrayList<>(removingUserSessions.values()));
    }

    public List<ResourceEntity> getDirtyResources() {
        return Collections.unmodifiableList(new ArrayList<>(dirtyResources.values()));
    }

    @Override
    public String getId() {
        return getObjectSegment().getId();
    }

    @Override
    public String getName() {
        return getObjectSegment().getName();
    }

    @Override
    public void setName(String name) {
        statusVerifier.checkForDeactiveStatus(this);
        if (name.equalsIgnoreCase(getName())) {
            return;
        }
        factory.checkForNameFormat(name);
        factory.checkForDuplicateName(name);
        getObjectSegment().setName(name);
        markAsDirty();
    }

    @Override
    public String getDescription() {
        return getObjectSegment().getDescription();
    }

    @Override
    public void setDescription(String description) {
        statusVerifier.checkForDeactiveStatus(this);
        description = description.trim();
        if (description.equalsIgnoreCase(getDescription())) {
            return;
        }
        getObjectSegment().setDescription(description);
        markAsDirty();
    }

    @Override
    public Date getCreatedDateTime() {
        return getObjectSegment().getCreationTime();
    }

    @Override
    public ApplicationStatus getStatus() {
        return getObjectSegment().getStatus();
    }

    @Override
    public void setStatus(ApplicationStatus status) {
        if (getStatus() == status) {
            return;
        }
        getObjectSegment().setStatus(status);
        markAsDirty();
    }

    @Override
    public PasswordPolicy getPasswordPolicy() {
        return getObjectSegment().getPasswordPolicy();
    }

    @Override
    public void setPasswordPolicy(PasswordPolicy policy) {
        statusVerifier.checkForDeactiveStatus(this);
        getObjectSegment().setPasswordPolicy(policy);
        markAsDirty();
    }

    @Override
    public UsernamePolicy createUsernamePolicy() {
        statusVerifier.checkForDeactiveStatus(this);
        UsernamePolicyEntity entity =
                usernameIdentifierPolicyFactory.generate(this);
        dirtyIdentifierPolicies.put(entity.getType(), entity);
        return entity;
    }

    @Override
    public EmailPolicy createEmailPolicy(
            boolean isVerificationRequired, List<String> domains) {
        statusVerifier.checkForDeactiveStatus(this);
        EmailPolicyEntity entity =
                emailIdentifierPolicyFactory.generate(this, isVerificationRequired, domains);
        dirtyIdentifierPolicies.put(entity.getType(), entity);
        return entity;
    }

    @Override
    public void updateIdentifierPolicy(IdentifierPolicy policy) {
        statusVerifier.checkForDeactiveStatus(this);
        IdentifierPolicyEntity entity = (IdentifierPolicyEntity) policy;
        dirtyIdentifierPolicies.put(entity.getType(), entity);
    }

    @Override
    public void removeIdentifierPolicy(IdentifierPolicy policy) {
        statusVerifier.checkForDeactiveStatus(this);
        IdentifierPolicyEntity entity = (IdentifierPolicyEntity) policy;
        entity.markAsVoid();
        dirtyIdentifierPolicies.put(entity.getType(), entity);
    }

    @Override
    public IdentifierPolicy fetchIdentifierPolicy(IdentifierType type) {
        return identifierPolicyRepository.findByType(this, type);
    }

    @Override
    public List<IdentifierPolicy> fetchAllIdentifierPolicies() {
        return identifierPolicyRepository.findAll(this);
    }

    @Override
    public OAuthIdentifierPolicy createOAuthIdentifierPolicy(OAuthPlatform platform,
                                                             Map<String, Object> configuration) {
        statusVerifier.checkForDeactiveStatus(this);
        OAuthIdentifierPolicyEntity entity =
                oAuthIdentifierPolicyFactory.generate(this, platform, configuration);
        dirtyOAuthIdentifierPolicies.put(entity.getPlatform(), entity);
        return entity;
    }

    @Override
    public void updateOAuthIdentifierPolicy(OAuthIdentifierPolicy policy) {
        statusVerifier.checkForDeactiveStatus(this);
        OAuthIdentifierPolicyEntity entity = (OAuthIdentifierPolicyEntity) policy;
        dirtyOAuthIdentifierPolicies.put(entity.getPlatform(), entity);
    }

    @Override
    public void removeOAuthIdentifierPolicy(OAuthIdentifierPolicy policy) {
        statusVerifier.checkForDeactiveStatus(this);
        OAuthIdentifierPolicyEntity entity = (OAuthIdentifierPolicyEntity) policy;
        entity.markAsVoid();
        dirtyOAuthIdentifierPolicies.put(entity.getPlatform(), entity);
    }

    @Override
    public OAuthIdentifierPolicy fetchOAuthIdentifierPolicy(OAuthPlatform platform) {
        return oAuthIdentifierPolicyRepository.findByPlatform(this, platform);
    }

    @Override
    public List<OAuthIdentifierPolicy> fetchAllOAuthIdentifierPolicies() {
        return oAuthIdentifierPolicyRepository.findAll(this);
    }

//    @Override
//    public Principal fetchPrincipalById(String id) {
//        return principalRepository.findById(this, id);
//    }

//    @Override
//    public User createUser() {
//        checkForInvalidStatus();
//        UserEntity entity = userFactory.generate(this);
//        dirtyPrincipals.put(entity.getId(), entity);
//        return entity;
//    }

//    @Override
//    public void updateUser(User user) {
//        checkForInvalidStatus();
//        PrincipalEntity entity = (PrincipalEntity) user;
//        dirtyPrincipals.put(entity.getId(), entity);
//    }

//    @Override
//    public void removeUser(User user) {
//        checkForInvalidStatus();
//        PrincipalEntity entity = (PrincipalEntity) user;
//        entity.markAsVoid();
//        dirtyPrincipals.put(entity.getId(), entity);
//        List<IdentifierPolicy> policies = fetchAllIdentifierPolicies();
//        for (IdentifierPolicy policy: policies) {
//            List<Identifier> identifiers = policy.fetchIdentifiersByUser(user);
//            for (Identifier identifier: identifiers) {
//                policy.removeIdentifier(identifier);
//            }
//            dirtyIdentifierPolicies.put(policy.getType(), (IdentifierPolicyEntity) policy);
//        }
//        List<OAuthIdentifierPolicy> oauthPolicies = fetchAllOAuthIdentifierPolicies();
//        for (OAuthIdentifierPolicy policy: oauthPolicies) {
//            List<OAuthIdentifier> identifiers = policy.fetchIdentifiersByUser(user);
//            for (OAuthIdentifier identifier: identifiers) {
//                policy.removeIdentifier(identifier);
//            }
//            dirtyOAuthIdentifierPolicies.put(policy.getPlatform(), (OAuthIdentifierPolicyEntity) policy);
//        }
//    }

//    @Override
//    public User fetchUserById(String id) {
//        return userRepository.findById(this, id);
//    }

//    @Override
//    public User fetchUserByIdentifier(String identifier) {
//        List<IdentifierPolicy> policies = identifierPolicyRepository.findAll(this);
//        for (IdentifierPolicy policy: policies) {
//            Identifier id = policy.fetchIdentifierByContent(identifier);
//            if (null != id) {
//                return id.getUser();
//            }
//        }
//        return null;
//    }

//    @Override
//    public User fetchUserByOAuthIdentifier(OAuthPlatform platform, String identifier) {
//        OAuthIdentifierPolicy policy =
//                oAuthIdentifierPolicyRepository.findByPlatform(this, platform);
//        if (null == policy) {
//            return null;
//        }
//        OAuthIdentifier id = policy.fetchIdentifierById(identifier);
//        if (null != id) {
//            return id.getUser();
//        }
//        return null;
//    }

//    @Override
//    public PaginatedResult<List<User>> fetchAllUsers() {
//        return userRepository.findByApplication(this);
//    }

//    @Override
//    public APIKey createAPIKey(User user, String name) {
//        checkForInvalidStatus();
//        APIKeyEntity entity = apiKeyFactory.generate(this, user, name);
//        dirtyPrincipals.put(entity.getId(), entity);
//        return entity;
//    }

//    @Override
//    public void updateAPIKey(APIKey apiKey) {
//        checkForInvalidStatus();
//        PrincipalEntity entity = (PrincipalEntity) apiKey;
//        dirtyPrincipals.put(entity.getId(), entity);
//    }

//    @Override
//    public void verifyAPIKey(String id, String secretKey) {
//        checkForInvalidStatus();
//        APIKey apiKey = fetchAPIKeyById(id);
//        if (null == apiKey
//                || !apiKey.getSecretKey().equals(secretKey)
//                || !apiKey.isActive()
//                || apiKey.getOwner() == null) {
//            throw BusinessError.raise(Errors.AUTHENTICATION_FAILED)
//                    .message("Failed to verify API key, " + id)
//                    .build();
//        }
//    }

//    @Override
//    public void removeAPIKey(APIKey apiKey) {
//        checkForInvalidStatus();
//        APIKeyEntity entity = (APIKeyEntity) apiKey;
//        entity.markAsVoid();
//        dirtyPrincipals.put(entity.getId(), entity);
//    }

//    @Override
//    public APIKey fetchAPIKeyById(String id) {
//        return apiKeyRepository.findById(this, id) ;
//    }

//    @Override
//    public List<APIKey> fetchAPIKeysByOwner(User user) {
//        return apiKeyRepository.findByOwner(this, user);
//    }

//    @Override
//    public UserSession createUserSession(User user, Map<String, Object> details, long timeout) {
//        checkForInvalidStatus();
//        UserSessionEntity entity = userSessionFactory.generate(user, details, timeout);
//        dirtyUserSessions.put(entity.getKey(), entity);
//        return entity;
//    }

//    @Override
//    public void removeUserSession(UserSession session) {
//        checkForInvalidStatus();
//        UserSessionEntity entity = (UserSessionEntity) session;
//        entity.markAsVoid();
//        dirtyUserSessions.put(entity.getKey(), entity);
//    }

//    @Override
//    public void removeAllUserSession(User user) {
//        checkForInvalidStatus();
//        removingUserSessions.put(user.getId().toLowerCase(), (UserEntity) user);
//    }

//    @Override
//    public UserSession fetchUserSessionByKey(String key) {
//        return userSessionRepository.findByKey(this, key);
//    }

//    @Override
//    public PaginatedResult<List<UserSession>> fetchUserSessionsByUser(User user) {
//        return userSessionRepository.findByOwner(user);
//    }

//    @Override
//    public Role addRole(String name) {
//        checkForInvalidStatus();
//        RoleEntity entity = roleFactory.generate(this, name);
//        dirtyPrincipals.put(entity.getId(), entity);
//        return entity;
//    }

//    @Override
//    public void updateRole(Role role) {
//        checkForInvalidStatus();
//        PrincipalEntity entity = (PrincipalEntity) role;
//        dirtyPrincipals.put(entity.getId(), entity);
//    }

//    @Override
//    public void removeRole(Role role) {
//        checkForInvalidStatus();
//        RoleEntity entity = (RoleEntity) role;
//        entity.markAsVoid();
//        dirtyPrincipals.put(entity.getId(), entity);
//    }

//    @Override
//    public Role fetchRoleById(String id) {
//        return roleRepository.findById(this, id);
//    }

//    @Override
//    public PaginatedResult<List<Role>> fetchAllRoles() {
//        return roleRepository.findAll(this);
//    }

//    @Override
//    public Resource createResource(String key, Principal owner) {
//        checkForInvalidStatus();
//        owner = resourceFactory.getRealOwner(owner);
//        String parentKey = readParentKey(key);
//        Resource parent = null;
//        if (null != parentKey) {
//            parent = getParentResource(parentKey, owner);
//        }
//        String name = readName(key);
//        ResourceEntity entity = resourceFactory.generate(this, name, owner, parent);
//        dirtyResources.put(entity.getKey(), entity);
//        return entity;
//    }

//    private Resource getParentResource(String key, Principal owner) {
//        Resource parent = fetchResourceByKey(key);
//        if (null == parent) {
//            return createResource(key, owner);
//        }
//        return parent;
//    }

//    private String readParentKey(String key) {
//        int position = key.lastIndexOf(ResourceKeySeparator.VALUE);
//        if (-1 == position) {
//            return null;
//        }
//        return key.substring(0, position);
//    }

//    private String readName(String key) {
//        int position = key.lastIndexOf(ResourceKeySeparator.VALUE);
//        if (-1 == position) {
//            return key;
//        }
//        return key.substring(position + 1);
//    }

//    @Override
//    public void updateResource(Resource resource) {
//        checkForInvalidStatus();
//        ResourceEntity entity = (ResourceEntity) resource;
//        dirtyResources.put(entity.getKey(), entity);
//    }

//    @Override
//    public void removeResource(Resource resource) {
//        checkForInvalidStatus();
//        ResourceEntity entity = (ResourceEntity) resource;
//        entity.markAsVoid();
//        dirtyResources.put(entity.getKey(), entity);
//    }

//    @Override
//    public Resource fetchResourceByKey(String key) {
//        return resourceRepository.findByKey(this, key);
//    }

//    @Override
//    public PaginatedResult<List<Resource>> fetchResourcesByOwner(Principal owner, Resource parentResource) {
//        return resourceRepository.findByOwner(this, owner, parentResource);
//    }

//    @Override
//    public PaginatedResult<List<Resource>> fetchPermissionAssignedResources(Principal principal) {
//        return resourceRepository.findPermissionAssignedResources(this, principal);
//    }

//    @Override
//    public PaginatedResult<List<Resource>> fetchAllResources(Resource parentResource) {
//        return resourceRepository.findAll(this, parentResource);
//    }

//    private void checkForInvalidStatus() {
//        if (getStatus() == ApplicationStatus.DEACTIVE) {
//            throw BusinessError.raise(Errors.INVALID_STATUS)
//                    .message("No operations allowed for inactive application.")
//                    .details("entity", Application.class.getSimpleName())
//                    .details("id", getId())
//                    .details("status", getStatus())
//                    .build();
//        }
//    }

}
