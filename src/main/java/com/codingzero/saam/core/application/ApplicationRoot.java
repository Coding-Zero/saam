package com.codingzero.saam.core.application;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.EmailPolicy;
import com.codingzero.saam.core.Identifier;
import com.codingzero.saam.core.IdentifierPolicy;
import com.codingzero.saam.core.OAuthIdentifier;
import com.codingzero.saam.core.OAuthIdentifierPolicy;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.core.Role;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.UserSession;
import com.codingzero.saam.core.UsernamePolicy;
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
    private Map<String, ResourceEntity> dirtyResources;

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
                           ResourceRepositoryService resourceRepository) {
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
        this.dirtyIdentifierPolicies = new HashMap<>();
        this.dirtyOAuthIdentifierPolicies = new HashMap<>();
        this.dirtyPrincipals = new HashMap<>();
        this.dirtyUserSessions = new HashMap<>();
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
        getObjectSegment().setPasswordPolicy(policy);
        markAsDirty();
    }

    @Override
    public UsernamePolicy createUsernamePolicy() {
        UsernamePolicyEntity entity =
                usernameIdentifierPolicyFactory.generate(this);
        dirtyIdentifierPolicies.put(entity.getType(), entity);
        return entity;
    }

    @Override
    public EmailPolicy createEmailPolicy(
            boolean isVerificationRequired, List<String> domains) {
        EmailPolicyEntity entity =
                emailIdentifierPolicyFactory.generate(this, isVerificationRequired, domains);
        dirtyIdentifierPolicies.put(entity.getType(), entity);
        return entity;
    }

    @Override
    public void updateIdentifierPolicy(IdentifierPolicy policy) {
        IdentifierPolicyEntity entity = (IdentifierPolicyEntity) policy;
        dirtyIdentifierPolicies.put(entity.getType(), entity);
    }

    @Override
    public void removeIdentifierPolicy(IdentifierPolicy policy) {
        IdentifierPolicyEntity entity = (IdentifierPolicyEntity) policy;
        entity.markAsVoid();
        dirtyIdentifierPolicies.put(entity.getType(), entity);
    }

    @Override
    public IdentifierPolicy fetchIdentifierPolicy(IdentifierType type) {
        return identifierPolicyRepository.findByType(this, type);
    }

    @Override
    public UsernamePolicy fetchUsernamePolicy() {
        return (UsernamePolicy) fetchIdentifierPolicy(IdentifierType.USERNAME);
    }

    @Override
    public EmailPolicy fetchEmailPolicy() {
        return (EmailPolicy) fetchIdentifierPolicy(IdentifierType.EMAIL);
    }

    @Override
    public List<IdentifierPolicy> fetchAllIdentifierPolicies() {
        return identifierPolicyRepository.findAll(this);
    }

    @Override
    public OAuthIdentifierPolicy createOAuthIdentifierPolicy(OAuthPlatform platform, Map<String, Object> configuration) {
        OAuthIdentifierPolicyEntity entity = oAuthIdentifierPolicyFactory.generate(this, platform, configuration);
        dirtyOAuthIdentifierPolicies.put(entity.getPlatform(), entity);
        return entity;
    }

    @Override
    public void updateOAuthIdentifierPolicy(OAuthIdentifierPolicy policy) {
        OAuthIdentifierPolicyEntity entity = (OAuthIdentifierPolicyEntity) policy;
        dirtyOAuthIdentifierPolicies.put(entity.getPlatform(), entity);
    }

    @Override
    public void removeOAuthIdentifierPolicy(OAuthIdentifierPolicy policy) {
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

    @Override
    public Principal fetchPrincipalById(String id) {
        return principalRepository.findById(this, id);
    }

    @Override
    public User createUser() {
        UserEntity entity = userFactory.generate(this);
        dirtyPrincipals.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void updateUser(User user) {
        PrincipalEntity entity = (PrincipalEntity) user;
        dirtyPrincipals.put(entity.getId(), entity);
    }

    @Override
    public void removeUser(User user) {
        PrincipalEntity entity = (PrincipalEntity) user;
        entity.markAsVoid();
        dirtyPrincipals.put(entity.getId(), entity);
    }

    @Override
    public User fetchUserById(String id) {
        return userRepository.findById(this, id);
    }

    @Override
    public User fetchUserByIdentifier(String identifier) {
        List<IdentifierPolicy> policies = identifierPolicyRepository.findAll(this);
        for (IdentifierPolicy policy: policies) {
            Identifier id = policy.fetchIdentifierById(identifier);
            if (null != id) {
                return id.getUser();
            }
        }
        return null;
    }

    @Override
    public User fetchUserByOAuthIdentifier(OAuthPlatform platform, String identifier) {
        OAuthIdentifierPolicy policy =
                oAuthIdentifierPolicyRepository.findByPlatform(this, platform);
        OAuthIdentifier id = policy.fetchIdentifierById(identifier);
        if (null != id) {
            return id.getUser();
        }
        return null;
    }

    @Override
    public PaginatedResult<List<User>> fetchAllUsers() {
        return userRepository.findAll(this);
    }

    @Override
    public APIKey createAPIKey(User user, String name) {
        APIKeyEntity entity = apiKeyFactory.generate(this, user, name);
        dirtyPrincipals.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void updateAPIKey(APIKey apiKey) {
        PrincipalEntity entity = (PrincipalEntity) apiKey;
        dirtyPrincipals.put(entity.getId(), entity);
    }

    @Override
    public void removeAPIKey(APIKey apiKey) {
        APIKeyEntity entity = (APIKeyEntity) apiKey;
        entity.markAsVoid();
        dirtyPrincipals.put(entity.getId(), entity);
    }

    @Override
    public APIKey fetchAPIKeyByKey(String key) {
        return apiKeyRepository.findByKey(this, key) ;
    }

    @Override
    public List<APIKey> fetchAPIKeysByOwner(User user) {
        return apiKeyRepository.findByOwner(this, user);
    }

    @Override
    public UserSession createUserSession(User user, Map<String, Object> details, long timeout) {
        UserSessionEntity entity = userSessionFactory.generate(user, details, timeout);
        dirtyUserSessions.put(entity.getKey(), entity);
        return entity;
    }

    @Override
    public void removeUserSession(UserSession session) {
        UserSessionEntity entity = (UserSessionEntity) session;
        entity.markAsVoid();
        dirtyUserSessions.put(entity.getKey(), entity);
    }

    @Override
    public void removeAllUserSession(User user) {
        userSessionRepository.remove(user);
    }

    @Override
    public UserSession fetchUserSessionByKey(String key) {
        return userSessionRepository.findByKey(this, key);
    }

    @Override
    public PaginatedResult<List<UserSession>> fetchUserSessionsByUser(User user) {
        return userSessionRepository.findByOwner(user);
    }

    @Override
    public Role addRole(String name) {
        RoleEntity entity = roleFactory.generate(this, name);
        dirtyPrincipals.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void updateRole(Role role) {
        PrincipalEntity entity = (PrincipalEntity) role;
        dirtyPrincipals.put(entity.getId(), entity);
    }

    @Override
    public void removeRole(Role role) {
        RoleEntity entity = (RoleEntity) role;
        entity.markAsVoid();
        dirtyPrincipals.put(entity.getId(), entity);
    }

    @Override
    public Role fetchRoleById(String id) {
        return roleRepository.findById(this, id);
    }

    @Override
    public PaginatedResult<List<Role>> fetchAllRoles() {
        return roleRepository.findAll(this);
    }

    @Override
    public Resource createResource(String name, Principal owner, Resource parent) {
        if (owner.getType() == PrincipalType.API_KEY) {
            APIKey apiKey = (APIKey) owner;
            owner = apiKey.getOwner();
        }
        ResourceEntity entity = resourceFactory.generate(this, name, owner, parent);
        dirtyResources.put(entity.getKey(), entity);
        return entity;
    }

    @Override
    public void updateResource(Resource resource) {
        ResourceEntity entity = (ResourceEntity) resource;
        dirtyResources.put(entity.getKey(), entity);
    }

    @Override
    public void removeResource(Resource resource) {
        ResourceEntity entity = (ResourceEntity) resource;
        entity.markAsVoid();
        dirtyResources.put(entity.getKey(), entity);
    }

    @Override
    public Resource fetchResourceByKey(String key) {
        return resourceRepository.findByKey(this, key);
    }

    @Override
    public PaginatedResult<List<Resource>> fetchResourcesByOwner(Principal owner, Resource parentResource) {
        return resourceRepository.findByOwner(this, owner, parentResource);
    }

    @Override
    public PaginatedResult<List<Resource>> fetchPermissionAssignedResources(Principal principal, Resource parentResource) {
        return resourceRepository.findPermissionAssignedResources(this, principal);
    }

    @Override
    public PaginatedResult<List<Resource>> fetchAllResources(Resource parentResource) {
        return resourceRepository.findAll(this, parentResource);
    }

}
