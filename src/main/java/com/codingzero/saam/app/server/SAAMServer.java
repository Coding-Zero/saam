package com.codingzero.saam.app.server;

import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.requests.APIKeyAddRequest;
import com.codingzero.saam.app.requests.APIKeyUpdateRequest;
import com.codingzero.saam.app.requests.APIKeyVerifyRequest;
import com.codingzero.saam.app.requests.ApplicationAddRequest;
import com.codingzero.saam.app.requests.ApplicationUpdateRequest;
import com.codingzero.saam.app.requests.CredentialLoginRequest;
import com.codingzero.saam.app.requests.EmailPolicyAddRequest;
import com.codingzero.saam.app.requests.EmailPolicyUpdateRequest;
import com.codingzero.saam.app.requests.IdentifierAddRequest;
import com.codingzero.saam.app.requests.IdentifierRemoveRequest;
import com.codingzero.saam.app.requests.IdentifierVerificationCodeGenerateRequest;
import com.codingzero.saam.app.requests.IdentifierVerifyRequest;
import com.codingzero.saam.app.requests.OAuthAccessTokenRequest;
import com.codingzero.saam.app.requests.OAuthAuthorizationUrlRequest;
import com.codingzero.saam.app.requests.OAuthIdentifierConnectRequest;
import com.codingzero.saam.app.requests.OAuthIdentifierDisconnectRequest;
import com.codingzero.saam.app.requests.OAuthIdentifierPolicyAddRequest;
import com.codingzero.saam.app.requests.OAuthIdentifierPolicyUpdateRequest;
import com.codingzero.saam.app.requests.OAuthLoginRequest;
import com.codingzero.saam.app.requests.PasswordChangeRequest;
import com.codingzero.saam.app.requests.PasswordPolicySetRequest;
import com.codingzero.saam.app.requests.PasswordResetCodeGenerateRequest;
import com.codingzero.saam.app.requests.PasswordResetRequest;
import com.codingzero.saam.app.requests.PermissionCheckRequest;
import com.codingzero.saam.app.requests.PermissionStoreRequest;
import com.codingzero.saam.app.requests.ResourceStoreRequest;
import com.codingzero.saam.app.requests.RoleAddRequest;
import com.codingzero.saam.app.requests.RoleUpdateRequest;
import com.codingzero.saam.app.requests.UserRegisterRequest;
import com.codingzero.saam.app.requests.UserRegisterWithIdentifierRequest;
import com.codingzero.saam.app.requests.UserRegisterWithOAuthRequest;
import com.codingzero.saam.app.requests.UserRoleUpdateRequest;
import com.codingzero.saam.app.requests.UserSessionCreateRequest;
import com.codingzero.saam.app.requests.UsernamePolicyAddRequest;
import com.codingzero.saam.app.requests.UsernamePolicyUpdateRequest;
import com.codingzero.saam.app.responses.APIKeyResponse;
import com.codingzero.saam.app.responses.ApplicationResponse;
import com.codingzero.saam.app.responses.IdentifierVerificationCodeResponse;
import com.codingzero.saam.app.responses.OAuthAccessTokenResponse;
import com.codingzero.saam.app.responses.PasswordResetCodeResponse;
import com.codingzero.saam.app.responses.PermissionCheckResponse;
import com.codingzero.saam.app.responses.PermissionResponse;
import com.codingzero.saam.app.responses.ResourceResponse;
import com.codingzero.saam.app.responses.RoleResponse;
import com.codingzero.saam.app.responses.UserResponse;
import com.codingzero.saam.app.responses.UserSessionResponse;
import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.Errors;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.common.PermissionType;
import com.codingzero.saam.common.ResourceKeySeparator;
import com.codingzero.saam.domain.APIKey;
import com.codingzero.saam.domain.APIKeyFactory;
import com.codingzero.saam.domain.APIKeyRepository;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.ApplicationFactory;
import com.codingzero.saam.domain.ApplicationRepository;
import com.codingzero.saam.domain.EmailPolicy;
import com.codingzero.saam.domain.Identifier;
import com.codingzero.saam.domain.IdentifierFactory;
import com.codingzero.saam.domain.IdentifierPolicy;
import com.codingzero.saam.domain.IdentifierRepository;
import com.codingzero.saam.domain.OAuthIdentifier;
import com.codingzero.saam.domain.OAuthIdentifierFactory;
import com.codingzero.saam.domain.OAuthIdentifierPolicy;
import com.codingzero.saam.domain.OAuthIdentifierRepository;
import com.codingzero.saam.domain.Permission;
import com.codingzero.saam.domain.Principal;
import com.codingzero.saam.domain.PrincipalRepository;
import com.codingzero.saam.domain.Resource;
import com.codingzero.saam.domain.ResourceFactory;
import com.codingzero.saam.domain.ResourceRepository;
import com.codingzero.saam.domain.Role;
import com.codingzero.saam.domain.RoleFactory;
import com.codingzero.saam.domain.RoleRepository;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.UserFactory;
import com.codingzero.saam.domain.UserRepository;
import com.codingzero.saam.domain.UserSession;
import com.codingzero.saam.domain.UserSessionFactory;
import com.codingzero.saam.domain.UserSessionRepository;
import com.codingzero.saam.domain.UsernamePolicy;
import com.codingzero.saam.domain.services.UserAuthenticator;
import com.codingzero.saam.infrastructure.OAuthAccessToken;
import com.codingzero.saam.infrastructure.data.OAuthPlatformAgent;
import com.codingzero.utilities.error.BusinessError;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.PaginatedResultMapper;
import com.codingzero.utilities.transaction.TransactionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SAAMServer implements SAAM {

    private TransactionManager transactionManager;
    private OAuthPlatformAgent oAuthPlatformAgent;
    private ApplicationFactory applicationFactory;
    private ApplicationRepository applicationRepository;
    private IdentifierFactory identifierFactory;
    private IdentifierRepository identifierRepository;
    private OAuthIdentifierFactory oAuthIdentifierFactory;
    private OAuthIdentifierRepository oAuthIdentifierRepository;
    private RoleFactory roleFactory;
    private RoleRepository roleRepository;
    private UserFactory userFactory;
    private UserRepository userRepository;
    private APIKeyFactory apiKeyFactory;
    private APIKeyRepository apiKeyRepository;
    private UserSessionFactory userSessionFactory;
    private UserSessionRepository userSessionRepository;
    private ResourceFactory resourceFactory;
    private ResourceRepository resourceRepository;
    private PrincipalRepository principalRepository;
    private ResponseMapper responseMapper;
    private UserAuthenticator userAuthenticator;

    public SAAMServer(SAAMBuilder builder) {
        this.transactionManager = builder.getTransactionManager();
        this.oAuthPlatformAgent = builder.getOAuthPlatformAgent();
        this.applicationFactory = builder.getApplicationFactory();
        this.applicationRepository = builder.getApplicationRepository();
        this.userFactory = builder.getUserFactory();
        this.userRepository = builder.getUserRepository();
        this.identifierFactory = builder.getIdentifierFactory();
        this.identifierRepository = builder.getIdentifierRepository();
        this.oAuthIdentifierFactory = builder.getoAuthIdentifierFactory();
        this.oAuthIdentifierRepository = builder.getOAuthIdentifierRepository();
        this.roleFactory = builder.getRoleFactory();
        this.roleRepository = builder.getRoleRepository();
        this.apiKeyFactory = builder.getApiKeyFactory();
        this.apiKeyRepository = builder.getApiKeyRepository();
        this.userSessionFactory = builder.getUserSessionFactory();
        this.userSessionRepository = builder.getUserSessionRepository();
        this.resourceFactory = builder.getResourceFactory();
        this.resourceRepository = builder.getResourceRepository();
        this.principalRepository = builder.getPrincipalRepository();
        this.responseMapper = builder.getResponseMapper();
        this.userAuthenticator = builder.getUserAuthenticator();
    }

    @Override
    public ApplicationResponse addApplication(ApplicationAddRequest request) {
        Application application = applicationFactory.generate(request.getName(), request.getDescription());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    private Application storeApplication(Application application) {
        transactionManager.start();
        try {
            application = applicationRepository.store(application);
            transactionManager.commit();
        } catch (RuntimeException e) {
            transactionManager.rollback();
            throw e;
        }
        return application;
    }

    private void removeApplication(Application application) {
        transactionManager.start();
        try {
            applicationRepository.remove(application);
            transactionManager.commit();
        } catch (RuntimeException e) {
            transactionManager.rollback();
            throw e;
        }
    }

    private Application getEnsuredApplicationById(String id) {
        Application application = applicationRepository.findById(id);
        if (null == application) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such application found.")
                    .details("entity", Application.class.getSimpleName())
                    .details("id", id)
                    .build();
        }
        return application;
    }

    @Override
    public ApplicationResponse updateApplication(ApplicationUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getId());
        application.setStatus(request.getStatus());
        application.setDescription(request.getDescription());
        application.setName(request.getName());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse setPasswordPolicy(PasswordPolicySetRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        application.setPasswordPolicy(request.getPasswordPolicy());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public void removeApplication(String id) {
        Application application = getEnsuredApplicationById(id);
        removeApplication(application);
    }

    @Override
    public ApplicationResponse getApplicationById(String id) {
        Application application = applicationRepository.findById(id);
        return responseMapper.toResponse(application);
    }

    @Override
    public PaginatedResult<List<ApplicationResponse>> listApplications() {
        PaginatedResult<List<Application>> result = applicationRepository.findAll();
        return new PaginatedResult<>(new PaginatedResultMapper<List<ApplicationResponse>, List<Application>>() {
            @Override
            protected List<ApplicationResponse> toResult(List<Application> source, Object[] arguments) {
                return toApplicationResponses(source);
            }
        }, result);
    }

    private List<ApplicationResponse> toApplicationResponses(List<Application> source) {
        List<ApplicationResponse> responses = new ArrayList<>(source.size());
        for (Application entity: source) {
            responses.add(responseMapper.toResponse(entity));
        }
        return responses;
    }

    @Override
    public String requestOAuthAuthorizationUrl(OAuthAuthorizationUrlRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        checkForApplicationStatus(application, request.getPlatform());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        return oAuthPlatformAgent.getAuthorizationUrl(
                request.getPlatform(), policy.getConfigurations(), request.getParameters());
    }

    @Override
    public OAuthAccessTokenResponse requestOAuthAccessToken(OAuthAccessTokenRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        checkForApplicationStatus(application, request.getPlatform());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        OAuthAccessToken token = oAuthPlatformAgent.requestAccessToken(
                policy.getPlatform(), policy.getConfigurations(), request.getParameters());
        return responseMapper.toResponse(application, token);
    }

    private void checkForApplicationStatus(Application application, OAuthPlatform platform) {
        if (application.getStatus() == ApplicationStatus.DEACTIVE) {
            throw BusinessError.raise(Errors.INVALID_STATUS)
                    .message("No OAuth operations allowed for inactive application.")
                    .details("entity", Application.class.getSimpleName())
                    .details("id", application.getId())
                    .details("status", application.getStatus())
                    .details("platform", platform)
                    .build();
        }
    }

    @Override
    public ApplicationResponse addUsernamePolicy(UsernamePolicyAddRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        application.createUsernamePolicy();
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse updateUsernamePolicy(UsernamePolicyUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        UsernamePolicy policy = (UsernamePolicy) getEnsuredIdentifierPolicy(application, IdentifierType.USERNAME);
        policy.setActive(request.isActive());
        application.updateIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    private IdentifierPolicy getEnsuredIdentifierPolicy(Application application, IdentifierType type) {
        IdentifierPolicy policy = application.fetchIdentifierPolicy(type);
        if (null == policy) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such identifier policy found.")
                    .details("type", type)
                    .details("entity", "IdentifierPolicy")
                    .build();
        }
        return policy;
    }

    @Override
    public ApplicationResponse addEmailPolicy(EmailPolicyAddRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        application.createEmailPolicy(
                request.isVerificationRequired(), request.getDomains());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse updateEmailPolicy(EmailPolicyUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        EmailPolicy policy = (EmailPolicy) getEnsuredIdentifierPolicy(application, IdentifierType.EMAIL);
        policy.setActive(request.isActive());
        policy.setDomains(request.getDomains());
        policy.setVerificationRequired(request.isVerificationRequired());
        application.updateIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse removeIdentifierPolicy(String applicationId, IdentifierType type) {
        Application application = getEnsuredApplicationById(applicationId);
        IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, type);
        application.removeIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse addOAuthIdentifierPolicy(OAuthIdentifierPolicyAddRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        application.createOAuthIdentifierPolicy(request.getPlatform(), request.getConfigurations());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse updateOAuthIdentifierPolicy(OAuthIdentifierPolicyUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        policy.setActive(request.isActive());
        policy.setConfigurations(request.getConfigurations());
        application.updateOAuthIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    private OAuthIdentifierPolicy getEnsuredOAuthIdentifierPolicy(Application application, OAuthPlatform platform) {
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(platform);
        if (null == policy) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such oauth identifier policy found.")
                    .details("platform", platform)
                    .details("entity", "OAuthIdentifierPolicy")
                    .build();
        }
        return policy;
    }

    private OAuthIdentifier getEnsuredOAuthIdentifier(OAuthIdentifierPolicy policy, String content) {
        OAuthIdentifier identifier = oAuthIdentifierRepository.findByKey(policy, content);
        if (null == identifier) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such oauth identifier found.")
                    .details("platform", policy.getPlatform())
                    .details("entity", "OAuthIdentifier")
                    .build();
        }
        return identifier;
    }

    @Override
    public ApplicationResponse removeOAuthIdentifierPolicy(String applicationId, OAuthPlatform platform) {
        Application application = getEnsuredApplicationById(applicationId);
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, platform);
        application.removeOAuthIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public UserResponse register(UserRegisterRequest request) {
        transactionManager.start();
        User user = null;
        try {
            Application application = getEnsuredApplicationById(request.getApplicationId());
            user = userFactory.generate(application);
            user.setPlayingRoles(getRoles(application, request.getRoleIds()));
            user = userRepository.store(user);
            transactionManager.commit();
        } catch (RuntimeException e) {
            transactionManager.rollback();
        }
        return responseMapper.toResponse(user, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    @Override
    public UserResponse registerWithIdentifier(UserRegisterWithIdentifierRequest request) {
        transactionManager.start();
        User user = null;
        Application application = null;
        try {
            application = getEnsuredApplicationById(request.getApplicationId());
            user = userFactory.generate(application);
            user.changePassword(request.getPassword(), request.getPassword());
            user.setPlayingRoles(getRoles(application, request.getRoleIds()));
            user = userRepository.store(user);
            setIdentifiers(user, request.getIdentifiers());
            transactionManager.commit();
        } catch (RuntimeException e) {
            transactionManager.rollback();
        }
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, Collections.EMPTY_LIST);
    }

    private void setIdentifiers(User user, Map<IdentifierType, String> identifiers) {
        Application application = user.getApplication();
        for (Map.Entry<IdentifierType, String> entry: identifiers.entrySet()) {
            IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, entry.getKey());
            Identifier identifier = identifierFactory.generate(policy, entry.getValue(), user);
            identifierRepository.store(identifier);
        }
    }

    @Override
    public UserResponse registerWithOAuth(UserRegisterWithOAuthRequest request) {
        transactionManager.start();
        User user = null;
        Application application = null;
        try {
            application = getEnsuredApplicationById(request.getApplicationId());
            user = userFactory.generate(application);
            user.setPlayingRoles(getRoles(application, request.getRoleIds()));
            user = userRepository.store(user);
            setOAuthIdentifiers(user, request.getOAuthIdentifiers());
            transactionManager.commit();
        } catch (RuntimeException e) {
            transactionManager.rollback();
        }
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, Collections.EMPTY_LIST, oAuthIdentifiers);
    }

    private void setOAuthIdentifiers(User user, Map<OAuthPlatform, UserRegisterWithOAuthRequest.OAuthIdentifier> identifiers) {
        Application application = user.getApplication();
        for (Map.Entry<OAuthPlatform, UserRegisterWithOAuthRequest.OAuthIdentifier> entry: identifiers.entrySet()) {
            OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, entry.getKey());
            OAuthIdentifier identifier = oAuthIdentifierFactory.generate(
                    policy,
                    entry.getValue().getIdentifier(),
                    entry.getValue().getProperties(),
                    user);
            oAuthIdentifierRepository.store(identifier);
        }
    }

    private List<Role> getRoles(Application application, List<String> roleIds) {
        List<Role> roles = new ArrayList<>(roleIds.size());
        for (String id: roleIds) {
            Role role = roleRepository.findById(application, id);
            if (null == role) {
                throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                        .message("No such role found, " + id)
                        .details("entity", "Role")
                        .details("id", id)
                        .build();
            }
            roles.add(role);
        }
        return roles;
    }

    @Override
    public void removeUser(String applicationId, String id) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = getEnsuredUser(application, id);
        userRepository.remove(user);
    }

    private User getEnsuredUser(Application application, String id) {
        User user = userRepository.findById(application, id);
        if (null == user) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No user found, " + id)
                    .details("entity", "User")
                    .details("id", id)
                    .build();
        }
        return user;
    }

    @Override
    public UserResponse getUserById(String applicationId, String id) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = getEnsuredUser(application, id);
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public UserResponse getUserByIdentifier(String applicationId, String identifier) {
        Application application = getEnsuredApplicationById(applicationId);
        Identifier id = getEnsuredIdentifier(application, identifier);
        User user = id.getUser();
        if (null == user) {
            return null;
        }
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public UserResponse getUserByOAuthIdentifier(String applicationId, OAuthPlatform platform, String identifier) {
        Application application = getEnsuredApplicationById(applicationId);
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, platform);
        OAuthIdentifier id = getEnsuredOAuthIdentifier(policy, identifier);
        User user = id.getUser();
        if (null == user) {
            return null;
        }
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public PaginatedResult<List<UserResponse>> listUsersByApplicationId(String applicationId) {
        Application application = getEnsuredApplicationById(applicationId);
        PaginatedResult<List<User>> result = userRepository.findByApplication(application);
        return new PaginatedResult<>(new PaginatedResultMapper<List<UserResponse>, List<User>>() {
            @Override
            protected List<UserResponse> toResult(List<User> source, Object[] arguments) {
                return toUserResponses(source);
            }
        }, result);
    }

    private List<UserResponse> toUserResponses(List<User> source) {
        List<UserResponse> responses = new ArrayList<>(source.size());
        for (User user: source) {
            List<Identifier> identifiers = identifierRepository.findByUser(user.getApplication(), user);
            List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(user.getApplication(), user);
            responses.add(responseMapper.toResponse(user, identifiers, oAuthIdentifiers));
        }
        return responses;
    }

    @Override
    public UserResponse updateRoles(UserRoleUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = userRepository.findById(application, request.getUserId());
        user.setPlayingRoles(getRoles(application, request.getRoleIds()));
        user = userRepository.store(user);
        List<Identifier> identifiers = identifierRepository.findByUser(user.getApplication(), user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public UserResponse addIdentifier(IdentifierAddRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = userRepository.findById(application, request.getUserId());
        IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, request.getType());
        Identifier identifier = identifierFactory.generate(policy, request.getIdentifier(), user);
        identifierRepository.store(identifier);
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public UserResponse removeIdentifier(IdentifierRemoveRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Identifier identifier = getEnsuredIdentifier(application, request.getIdentifier());
        identifierRepository.remove(identifier);
        User user = getEnsuredUser(application, request.getUserId());
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public IdentifierVerificationCodeResponse generateVerificationCode(
            IdentifierVerificationCodeGenerateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Identifier identifier = getEnsuredIdentifier(application, request.getIdentifier());
        identifier.generateVerificationCode(request.getTimeout());
        identifierRepository.store(identifier);
        return responseMapper.toResponse(identifier);
    }

    private Identifier getEnsuredIdentifier(Application application, String content) {
        Identifier identifier = identifierRepository.findByKey(application, content);
        if (null == identifier) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such identifier found, " + content)
                    .details("applicationId", application.getId())
                    .details("identifier", content)
                    .details("type", "Identifier")
                    .build();
        }
        return identifier;
    }

    @Override
    public UserResponse verifyIdentifier(IdentifierVerifyRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Identifier identifier = getEnsuredIdentifier(application, request.getIdentifier());
        identifier.verify(request.getVerificationCode());
        User user = identifier.getUser();
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public UserResponse connectOAuthIdentifier(OAuthIdentifierConnectRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        OAuthIdentifier identifier = oAuthIdentifierRepository.findByKey(policy, request.getIdentifier());
        if (null == identifier) {
            identifier = oAuthIdentifierFactory.generate(
                    policy, request.getIdentifier(), request.getProperties(), user);
        } else {
            identifier.setProperties(request.getProperties());
        }
        oAuthIdentifierRepository.store(identifier);
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public UserResponse disconnectOAuthIdentifier(OAuthIdentifierDisconnectRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        OAuthIdentifier identifier = getEnsuredOAuthIdentifier(policy, request.getIdentifier());
        oAuthIdentifierRepository.remove(identifier);
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public UserResponse changePassword(PasswordChangeRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        user.changePassword(request.getOldPassword(), request.getNewPassword());
        user = userRepository.store(user);
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public PasswordResetCodeResponse generateResetCode(PasswordResetCodeGenerateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        Identifier identifier = getEnsuredIdentifier(application, request.getIdentifier());
        PasswordResetCode resetCode = user.generatePasswordResetCode(identifier, request.getTimeout());
        user = userRepository.store(user);
        return responseMapper.toResponse(user, identifier, resetCode);
    }

    @Override
    public UserResponse resetPassword(PasswordResetRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        user.resetPassword(request.getResetCode(), request.getNewPassword());
        user = userRepository.store(user);
        List<Identifier> identifiers = identifierRepository.findByUser(application, user);
        List<OAuthIdentifier> oAuthIdentifiers = oAuthIdentifierRepository.findByUser(application, user);
        return responseMapper.toResponse(user, identifiers, oAuthIdentifiers);
    }

    @Override
    public APIKeyResponse addAPIKey(APIKeyAddRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        APIKey apiKey = apiKeyFactory.generate(application, user, request.getName());
        apiKeyRepository.store(apiKey);
        return responseMapper.toResponse(apiKey);
    }

    @Override
    public APIKeyResponse updateAPIKey(APIKeyUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        APIKey apiKey = getEnsuredAPIKey(application, request.getId());
        apiKey.setName(request.getName());
        apiKey.setActive(request.isActive());
        apiKey = apiKeyRepository.store(apiKey);
        return responseMapper.toResponse(apiKey);
    }

    @Override
    public APIKeyResponse verifyAPIKey(APIKeyVerifyRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        APIKey apiKey = getEnsuredAPIKey(application, request.getId());
        apiKey.verify(request.getSecretKey());
        apiKey = apiKeyRepository.store(apiKey);
        return responseMapper.toResponse(apiKey);
    }

    private APIKey getEnsuredAPIKey(Application application, String id) {
        APIKey apiKey = apiKeyRepository.findById(application, id);
        if (null == apiKey) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such API key found.")
                    .details("entity", APIKey.class.getSimpleName())
                    .details("applicationId", application.getId())
                    .details("id", id)
                    .build();
        }
        return apiKey;
    }

    @Override
    public void removeAPIKeyById(String applicationId, String id) {
        Application application = getEnsuredApplicationById(applicationId);
        APIKey apiKey = getEnsuredAPIKey(application, id);
        apiKeyRepository.remove(apiKey);
    }

    @Override
    public APIKeyResponse getAPIKeyById(String applicationId, String id) {
        Application application = getEnsuredApplicationById(applicationId);
        APIKey apiKey = apiKeyRepository.findById(application, id);
        return responseMapper.toResponse(apiKey);
    }

    @Override
    public List<APIKeyResponse> listAPIKeysByApplicationIdAndUserId(String applicationId, String userId) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = getEnsuredUser(application, userId);
        List<APIKey> apiKeys = apiKeyRepository.findByOwner(user);
        List<APIKeyResponse> responses = new ArrayList<>();
        for (APIKey apiKey: apiKeys) {
            responses.add(responseMapper.toResponse(apiKey));
        }
        return Collections.unmodifiableList(responses);
    }

    @Override
    public UserSessionResponse login(CredentialLoginRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Identifier identifier = identifierRepository.findByKey(application, request.getIdentifier());
        if (null == identifier) {
            throw BusinessError.raise(Errors.AUTHENTICATION_FAILED)
                    .message("Failed to login.")
                    .build();
        }
        User user = identifier.getUser();
        if (user.verifyPassword(request.getPassword())) {
            throw BusinessError.raise(Errors.AUTHENTICATION_FAILED)
                    .message("Failed to login.")
                    .build();
        }
        UserSession session = userSessionFactory.generate(
                application, user, request.getSessionDetails(), request.getSessionTimeout());
        session = userSessionRepository.store(session);
        return responseMapper.toResponse(session);
    }

    @Override
    public UserSessionResponse login(OAuthLoginRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        OAuthIdentifier identifier = oAuthIdentifierRepository.findByKey(policy, request.getIdentifier());
        if (null == identifier) {
            throw BusinessError.raise(Errors.AUTHENTICATION_FAILED)
                    .message("Failed to login.")
                    .build();
        }
        User user = identifier.getUser();
        UserSession session = userSessionFactory.generate(
                application, user, request.getSessionDetails(), request.getSessionTimeout());
        session = userSessionRepository.store(session);
        return responseMapper.toResponse(session);
    }

    @Override
    public UserSessionResponse createUserSession(UserSessionCreateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        UserSession session = userSessionFactory.generate(
                application, user, request.getDetails(), request.getSessionTimeout());
        session = userSessionRepository.store(session);
        return responseMapper.toResponse(session);
    }

    @Override
    public void removeUserSessionByKey(String applicationId, String sessionKey) {
        Application application = getEnsuredApplicationById(applicationId);
        UserSession session = getEnsuredUserSession(application, sessionKey);
        userSessionRepository.remove(session);
    }

    private UserSession getEnsuredUserSession(Application application, String key) {
        UserSession session = userSessionRepository.findByKey(application, key);
        if (null == session) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such user usersession found.")
                    .details("entity", UserSession.class.getSimpleName())
                    .details("applicationId", application.getId())
                    .details("key", key)
                    .build();
        }
        return session;
    }

    @Override
    public void removeUserSessionsByUserId(String applicationId, String userId) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = getEnsuredUser(application, userId);
        userSessionRepository.removeByUser(user);
    }

    @Override
    public UserSessionResponse getUserSessionByKey(String applicationId, String key) {
        Application application = getEnsuredApplicationById(applicationId);
        UserSession session = userSessionRepository.findByKey(application, key);
        return responseMapper.toResponse(session);
    }

    @Override
    public PaginatedResult<List<UserSessionResponse>> listUserSessionsByUserId(String applicationId, String userId) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = getEnsuredUser(application, userId);
        PaginatedResult<List<UserSession>> result = userSessionRepository.findByOwner(user);
        return new PaginatedResult<>(new PaginatedResultMapper<List<UserSessionResponse>, List<UserSession>>() {
            @Override
            protected List<UserSessionResponse> toResult(List<UserSession> source, Object[] arguments) {
                return toUserSessionResponses(source);
            }
        }, result);
    }

    private List<UserSessionResponse> toUserSessionResponses(List<UserSession> source) {
        List<UserSessionResponse> responses = new ArrayList<>(source.size());
        for (UserSession entity: source) {
            responses.add(responseMapper.toResponse(entity));
        }
        return responses;
    }

    @Override
    public RoleResponse addRole(RoleAddRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Role role = roleFactory.generate(application, request.getName());
        role = roleRepository.store(role);
        return responseMapper.toResponse(role);
    }

    @Override
    public RoleResponse updateRole(RoleUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Role role = getEnsuredRole(application, request.getId());
        role.setName(request.getName());
        role = roleRepository.store(role);
        return responseMapper.toResponse(role);
    }

    private Role getEnsuredRole(Application application, String id) {
        Role role = roleRepository.findById(application, id);
        if (null == role) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such role found.")
                    .details("entity", Role.class.getSimpleName())
                    .details("applicationId", application.getId())
                    .details("id", id)
                    .build();
        }
        return role;
    }

    @Override
    public void removeRole(String applicationId, String roleId) {
        Application application = getEnsuredApplicationById(applicationId);
        Role role = getEnsuredRole(application, roleId);
        roleRepository.remove(role);
    }

    @Override
    public RoleResponse getRoleById(String applicationId, String id) {
        Application application = getEnsuredApplicationById(applicationId);
        Role role = roleRepository.findById(application, id);
        return responseMapper.toResponse(role);
    }

    @Override
    public PaginatedResult<List<RoleResponse>> listRoles(String applicationId) {
        Application application = getEnsuredApplicationById(applicationId);
        PaginatedResult<List<Role>> result = roleRepository.findAll(application);
        return new PaginatedResult<>(new PaginatedResultMapper<List<RoleResponse>, List<Role>>() {
            @Override
            protected List<RoleResponse> toResult(List<Role> source, Object[] arguments) {
                return toRoleResponses(source);
            }
        }, result);
    }

    private List<RoleResponse> toRoleResponses(List<Role> source) {
        List<RoleResponse> responses = new ArrayList<>(source.size());
        for (Role entity: source) {
            responses.add(responseMapper.toResponse(entity));
        }
        return responses;
    }

    @Override
    public ResourceResponse storeResource(ResourceStoreRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Principal owner = getEnsuredPrincipal(application, request.getOwnerId());
        Resource resource = resourceRepository.findByKey(application, request.getKey());
        if (null == resource) {
            resource = resourceFactory.generate(application, request.getKey(), owner);
        } else {
            resource.setOwner(owner);
        }
        resourceRepository.store(resource);
        return responseMapper.toResponse(resource);
    }

    private Principal getEnsuredPrincipal(Application application, String id) {
        Principal principal = principalRepository.findById(application, id);
        if (null == principal) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such principal found, " + id)
                    .details("entity", Principal.class.getSimpleName())
                    .details("applicationId", application.getId())
                    .details("id", id)
                    .build();
        }
        return principal;
    }

    private String[] readParentKeyAndName(String key) {
        int position = key.lastIndexOf(ResourceKeySeparator.VALUE);
        if (-1 == position) {
            return new String[] {null, key};
        }
        String parentKey = key.substring(0, position);
        String name = key.substring(position + 1);
        return new String[] {parentKey, name};
    }

    private Resource getEnsuredResource(Application application, String key) {
        Resource resource = resourceRepository.findByKey(application, key);
        if (null == resource) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such parent resource found, " + key)
                    .details("type", "Resource")
                    .details("applicationId", application.getId())
                    .details("key", key)
                    .build();
        }
        return resource;
    }

    @Override
    public void removeResource(String applicationId, String key) {
        Application application = getEnsuredApplicationById(applicationId);
        Resource resource = getEnsuredResource(application, key);
        resourceRepository.remove(resource);
    }

    @Override
    public ResourceResponse getResourceByKey(String applicationId, String key) {
        Application application = getEnsuredApplicationById(applicationId);
        Resource resource = resourceRepository.findByKey(application, key);
        return responseMapper.toResponse(resource);
    }

    @Override
    public PaginatedResult<List<ResourceResponse>> getResourcesByOwnerId(
            String applicationId, String ownerId, String parentKey) {
        Application application = getEnsuredApplicationById(applicationId);
        Principal owner = getEnsuredPrincipal(application, ownerId);
        Resource parent = null;
        if (null != parentKey && parentKey.trim().length() > 0) {
            parent = getEnsuredResource(application, parentKey);
        }
        PaginatedResult<List<Resource>> result = resourceRepository.findByOwner(application, owner, parent);
        return new PaginatedResult<>(new PaginatedResultMapper<List<ResourceResponse>, List<Resource>>() {
            @Override
            protected List<ResourceResponse> toResult(List<Resource> source, Object[] arguments) {
                return toResourceResponses(source);
            }
        }, result);
    }

    private List<ResourceResponse> toResourceResponses(List<Resource> source) {
        if (null == source) {
            return new ArrayList<>(0);
        }
        List<ResourceResponse> responses = new ArrayList<>(source.size());
        for (Resource entity: source) {
            responses.add(responseMapper.toResponse(entity));
        }
        return responses;
    }

    @Override
    public PaginatedResult<List<ResourceResponse>> getGrantedResources(
            String applicationId, String principalId) {
        Application application = getEnsuredApplicationById(applicationId);
        Principal principal = getEnsuredPrincipal(application, principalId);
        PaginatedResult<List<Resource>> result =
                resourceRepository.findPermissionAssignedResources(application, principal);
        return new PaginatedResult<>(new PaginatedResultMapper<List<ResourceResponse>, List<Resource>>() {
            @Override
            protected List<ResourceResponse> toResult(List<Resource> source, Object[] arguments) {
                return toResourceResponses(source);
            }
        }, result);
    }

    @Override
    public PaginatedResult<List<ResourceResponse>> listResources(String applicationId, String parentKey) {
        Application application = getEnsuredApplicationById(applicationId);
        Resource parent = null;
        if (null != parentKey && parentKey.trim().length() > 0) {
            parent = getEnsuredResource(application, parentKey);
        }
        PaginatedResult<List<Resource>> result = resourceRepository.findByApplication(application, parent);
        return new PaginatedResult<>(new PaginatedResultMapper<List<ResourceResponse>, List<Resource>>() {
            @Override
            protected List<ResourceResponse> toResult(List<Resource> source, Object[] arguments) {
                return toResourceResponses(source);
            }
        }, result);
    }

    @Override
    public PermissionResponse storePermission(PermissionStoreRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Principal principal = getEnsuredPrincipal(application, request.getPrincipalId());
        Resource resource = getEnsuredResource(application, request.getResourceKey());
        Permission permission = resource.fetchPermissionById(principal);
        if (null == permission) {
            permission = resource.assignPermission(principal, request.getActions());
        } else {
            permission.setActions(request.getActions());
            resource.changePermission(permission);
        }
        resourceRepository.store(resource);
        return responseMapper.toResponse(permission);
    }

    @Override
    public void removePermission(String applicationId, String resourceKey, String principalId) {
        Application application = getEnsuredApplicationById(applicationId);
        Principal principal = getEnsuredPrincipal(application, principalId);
        Resource resource = getEnsuredResource(application, resourceKey);
        Permission permission = getEnsuredPermission(resource, principal);
        resource.removePermission(permission);
        resourceRepository.store(resource);
    }

    private Permission getEnsuredPermission(Resource resource, Principal principal) {
        Permission permission = resource.fetchPermissionById(principal);
        if (null == permission) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such permission resource found")
                    .details("type", "Permission")
                    .details("applicationId", resource.getApplication().getId())
                    .details("resourceKey", resource.getKey())
                    .details("principalId", principal.getId())
                    .build();
        }
        return permission;
    }

    @Override
    public PermissionResponse getPermissionByPrincipalId(
            String applicationId, String resourceKey, String principalId) {
        Application application = getEnsuredApplicationById(applicationId);
        Principal principal = getEnsuredPrincipal(application, principalId);
        Resource resource = getEnsuredResource(application, resourceKey);
        Permission permission = resource.fetchPermissionById(principal);
        return responseMapper.toResponse(permission);
    }

    @Override
    public PaginatedResult<List<PermissionResponse>> listPermissions(
            String applicationId, String resourceKey) {
        Application application = getEnsuredApplicationById(applicationId);
        Resource resource = getEnsuredResource(application, resourceKey);
        PaginatedResult<List<Permission>> result = resource.fetchAllPermissions();
        return new PaginatedResult<>(new PaginatedResultMapper<List<PermissionResponse>, List<Permission>>() {
            @Override
            protected List<PermissionResponse> toResult(List<Permission> source, Object[] arguments) {
                return toPermissionResponses(source);
            }
        }, result, resource);
    }

    private List<PermissionResponse> toPermissionResponses(List<Permission> source) {
        List<PermissionResponse> responses = new ArrayList<>(source.size());
        for (Permission entity: source) {
            responses.add(responseMapper.toResponse(entity));
        }
        return responses;
    }

    @Override
    public PermissionCheckResponse checkPermission(PermissionCheckRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Principal principal = getEnsuredPrincipal(application, request.getPrincipalId());
        Resource resource = getEnsuredResource(application, request.getResourceKey());
        PermissionType result = resource.verifyPermission(principal, request.getActionCode());
        return responseMapper.toResponse(principal, resource, request.getActionCode(), result);
    }
}
