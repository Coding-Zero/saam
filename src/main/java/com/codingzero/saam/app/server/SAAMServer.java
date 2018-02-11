package com.codingzero.saam.app.server;

import com.codingzero.saam.app.APIKeyAddRequest;
import com.codingzero.saam.app.APIKeyResponse;
import com.codingzero.saam.app.APIKeyUpdateRequest;
import com.codingzero.saam.app.APIKeyVerifyRequest;
import com.codingzero.saam.app.ApplicationAddRequest;
import com.codingzero.saam.app.ApplicationResponse;
import com.codingzero.saam.app.ApplicationUpdateRequest;
import com.codingzero.saam.app.CredentialLoginRequest;
import com.codingzero.saam.app.EmailPolicyAddRequest;
import com.codingzero.saam.app.EmailPolicyUpdateRequest;
import com.codingzero.saam.app.IdentifierAddRequest;
import com.codingzero.saam.app.IdentifierRemoveRequest;
import com.codingzero.saam.app.IdentifierVerificationCodeGenerateRequest;
import com.codingzero.saam.app.IdentifierVerificationCodeResponse;
import com.codingzero.saam.app.IdentifierVerifyRequest;
import com.codingzero.saam.app.OAuthAccessTokenRequest;
import com.codingzero.saam.app.OAuthAccessTokenResponse;
import com.codingzero.saam.app.OAuthAuthorizationUrlRequest;
import com.codingzero.saam.app.OAuthIdentifierConnectRequest;
import com.codingzero.saam.app.OAuthIdentifierDisconnectRequest;
import com.codingzero.saam.app.OAuthIdentifierPolicyAddRequest;
import com.codingzero.saam.app.OAuthIdentifierPolicyUpdateRequest;
import com.codingzero.saam.app.OAuthLoginRequest;
import com.codingzero.saam.app.PasswordChangeRequest;
import com.codingzero.saam.app.PasswordPolicyUpdateRequest;
import com.codingzero.saam.app.PasswordResetCodeGenerateRequest;
import com.codingzero.saam.app.PasswordResetCodeResponse;
import com.codingzero.saam.app.PasswordResetRequest;
import com.codingzero.saam.app.PermissionCheckRequest;
import com.codingzero.saam.app.PermissionCheckResponse;
import com.codingzero.saam.app.PermissionResponse;
import com.codingzero.saam.app.PermissionStoreRequest;
import com.codingzero.saam.app.ResourceResponse;
import com.codingzero.saam.app.ResourceStoreRequest;
import com.codingzero.saam.app.RoleAddRequest;
import com.codingzero.saam.app.RoleResponse;
import com.codingzero.saam.app.RoleUpdateRequest;
import com.codingzero.saam.app.SAAM;
import com.codingzero.saam.app.UserRegisterRequest;
import com.codingzero.saam.app.UserResponse;
import com.codingzero.saam.app.UserRoleUpdateRequest;
import com.codingzero.saam.app.UserSessionCreateRequest;
import com.codingzero.saam.app.UserSessionResponse;
import com.codingzero.saam.app.UsernamePolicyAddRequest;
import com.codingzero.saam.app.UsernamePolicyUpdateRequest;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.common.PermissionType;
import com.codingzero.saam.common.ResourceKeySeparator;
import com.codingzero.saam.core.APIKey;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.ApplicationFactory;
import com.codingzero.saam.core.ApplicationRepository;
import com.codingzero.saam.core.EmailPolicy;
import com.codingzero.saam.core.Identifier;
import com.codingzero.saam.core.IdentifierPolicy;
import com.codingzero.saam.core.OAuthIdentifier;
import com.codingzero.saam.core.OAuthIdentifierPolicy;
import com.codingzero.saam.core.Permission;
import com.codingzero.saam.core.Principal;
import com.codingzero.saam.core.Resource;
import com.codingzero.saam.core.Role;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.UserSession;
import com.codingzero.saam.core.UsernamePolicy;
import com.codingzero.saam.core.services.UserAuthenticator;
import com.codingzero.saam.infrastructure.OAuthAccessToken;
import com.codingzero.saam.infrastructure.database.spi.OAuthPlatformAgent;
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
    private ResponseMapper responseMapper;
    private UserAuthenticator userAuthenticator;

    public SAAMServer(SAAMBuilder builder) {
        this.transactionManager = builder.getTransactionManager();
        this.oAuthPlatformAgent = builder.getOAuthPlatformAgent();
        this.applicationFactory = builder.getApplicationFactory();
        this.applicationRepository = builder.getApplicationRepository();
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
    public ApplicationResponse updatePasswordPolicy(PasswordPolicyUpdateRequest request) {
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
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        return oAuthPlatformAgent.getAuthorizationUrl(
                request.getPlatform(), policy.getConfigurations(), request.getParameters());
    }

    @Override
    public OAuthAccessTokenResponse requestOAuthAccessToken(OAuthAccessTokenRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        OAuthAccessToken token = oAuthPlatformAgent.requestAccessToken(
                policy.getPlatform(), policy.getConfigurations(), request.getParameters());
        return responseMapper.toResponse(application, token);
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
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.createUser();
        setIdentifiers(user, request.getIdentifiers());
        setOAuthIdentifiers(user, request.getOAuthIdentifiers());
        user.changePassword(request.getPassword(), request.getPassword());
        user.setPlayingRoles(getRoles(application, request.getRoleIds()));
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    private void setIdentifiers(User user, Map<IdentifierType, String> identifiers) {
        Application application = user.getApplication();
        for (Map.Entry<IdentifierType, String> entry: identifiers.entrySet()) {
            IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, entry.getKey());
            policy.addIdentifier(entry.getValue(), user);
            application.updateIdentifierPolicy(policy);
        }
    }

    private void setOAuthIdentifiers(User user, Map<OAuthPlatform, UserRegisterRequest.OAuthIdentifier> identifiers) {
        Application application = user.getApplication();
        for (Map.Entry<OAuthPlatform, UserRegisterRequest.OAuthIdentifier> entry: identifiers.entrySet()) {
            OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, entry.getKey());
            policy.addIdentifier(entry.getValue().getIdentifier(), entry.getValue().getProperties(), user);
            application.updateOAuthIdentifierPolicy(policy);
        }
    }

    private List<Role> getRoles(Application application, List<String> roleIds) {
        List<Role> roles = new ArrayList<>(roleIds.size());
        for (String id: roleIds) {
            Role role = application.fetchRoleById(id);
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
        application.removeUser(user);
        storeApplication(application);
    }

    private User getEnsuredUser(Application application, String id) {
        User user = application.fetchUserById(id);
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
        User user = application.fetchUserById(id);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByIdentifier(String applicationId, String identifier) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = application.fetchUserByIdentifier(identifier);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByOAuthIdentifier(String applicationId, OAuthPlatform platform, String identifier) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = application.fetchUserByOAuthIdentifier(platform, identifier);
        return responseMapper.toResponse(user);
    }

    @Override
    public PaginatedResult<List<UserResponse>> listUsersByApplicationId(String applicationId) {
        Application application = getEnsuredApplicationById(applicationId);
        PaginatedResult<List<User>> result = application.fetchAllUsers();
        return new PaginatedResult<>(new PaginatedResultMapper<List<UserResponse>, List<User>>() {
            @Override
            protected List<UserResponse> toResult(List<User> source, Object[] arguments) {
                return toUserResponses(source);
            }
        }, result);
    }

    private List<UserResponse> toUserResponses(List<User> source) {
        List<UserResponse> responses = new ArrayList<>(source.size());
        for (User entity: source) {
            responses.add(responseMapper.toResponse(entity));
        }
        return responses;
    }

    @Override
    public UserResponse updateRoles(UserRoleUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        user.setPlayingRoles(getRoles(application, request.getRoleIds()));
        application.updateUser(user);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse addIdentifier(IdentifierAddRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, request.getType());
        policy.addIdentifier(request.getIdentifier(), user);
        application.updateIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse removeIdentifier(IdentifierRemoveRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, request.getType());
        Identifier identifier = getEnsuredIdentifer(policy, user, request.getIdentifier());
        policy.removeIdentifier(identifier);
        application.updateIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public IdentifierVerificationCodeResponse generateVerificationCode(
            IdentifierVerificationCodeGenerateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, request.getIdentifierType());
        Identifier identifier = getEnsuredIdentifer(policy, user, request.getIdentifier());
        identifier.generateVerificationCode(request.getTimeout());
        policy.updateIdentifier(identifier);
        application.updateIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(identifier);
    }

    private Identifier getEnsuredIdentifer(IdentifierPolicy policy, User user, String content) {
        Identifier identifier = policy.fetchIdentifierByUserAndId(user, content);
        if (null == identifier) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such identifier found, " + content + " for user, " + user.getId())
                    .details("applicationId", policy.getApplication().getId())
                    .details("identifierType", policy.getType())
                    .details("userId", user.getId())
                    .details("identifier", content)
                    .details("type", "Identifier")
                    .build();
        }
        return identifier;
    }

    @Override
    public UserResponse verifyIdentifier(IdentifierVerifyRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, request.getIdentifierType());
        Identifier identifier = getEnsuredIdentifer(policy, user, request.getIdentifier());
        identifier.verify(request.getVerificationCode());
        policy.updateIdentifier(identifier);
        application.updateIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse connectOAuthIdentifier(OAuthIdentifierConnectRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        OAuthIdentifier identifier = policy.fetchIdentifierByUserAndId(user, request.getIdentifier());
        if (null == identifier) {
            policy.addIdentifier(request.getIdentifier(), request.getProperties(), user);
        } else {
            identifier.setProperties(request.getProperties());
            policy.updateIdentifier(identifier);
        }
        application.updateOAuthIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse disconnectOAuthIdentifier(OAuthIdentifierDisconnectRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        OAuthIdentifierPolicy policy = getEnsuredOAuthIdentifierPolicy(application, request.getPlatform());
        OAuthIdentifier identifier = policy.fetchIdentifierByUserAndId(user, request.getIdentifier());
        if (null == identifier) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such oauth identifier found, " + request.getIdentifier() + " for user, " + user.getId())
                    .details("type", "OAuthIdentifier")
                    .details("userId", user.getId())
                    .details("platform", request.getPlatform())
                    .details("identifier", request.getIdentifier())
                    .build();
        }
        policy.removeIdentifier(identifier);
        application.updateOAuthIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse changePassword(PasswordChangeRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        user.changePassword(request.getOldPassword(), request.getNewPassword());
        application.updateUser(user);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public PasswordResetCodeResponse generateResetCode(PasswordResetCodeGenerateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        IdentifierPolicy policy = getEnsuredIdentifierPolicy(application, request.getIdentifierType());
        User user = application.fetchUserById(request.getUserId());
        Identifier identifier = getEnsuredIdentifer(policy, user, request.getIdentifier());
        PasswordResetCode resetCode = user.generatePasswordResetCode(identifier, request.getTimeout());
        application.updateUser(user);
        storeApplication(application);
        return responseMapper.toResponse(user, identifier, resetCode);
    }

    @Override
    public UserResponse resetPassword(PasswordResetRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        user.resetPassword(request.getResetCode(), request.getNewPassword());
        application.updateUser(user);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public APIKeyResponse addAPIKey(APIKeyAddRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        APIKey apiKey = application.createAPIKey(user, request.getName());
        storeApplication(application);
        return responseMapper.toResponse(apiKey);
    }

    @Override
    public APIKeyResponse updateAPIKey(APIKeyUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        APIKey apiKey = getEnsuredAPIKey(application, request.getId());
        apiKey.setName(request.getName());
        apiKey.setActive(request.isActive());
        application.updateAPIKey(apiKey);
        storeApplication(application);
        return responseMapper.toResponse(apiKey);
    }

    @Override
    public void verifyAPIKey(APIKeyVerifyRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        application.verifyAPIKey(request.getId(), request.getSecretKey());
    }

    private APIKey getEnsuredAPIKey(Application application, String id) {
        APIKey apiKey = application.fetchAPIKeyById(id);
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
        application.removeAPIKey(apiKey);
        storeApplication(application);
    }

    @Override
    public APIKeyResponse getAPIKeyById(String applicationId, String id) {
        Application application = getEnsuredApplicationById(applicationId);
        APIKey apiKey = application.fetchAPIKeyById(id);
        return responseMapper.toResponse(apiKey);
    }

    @Override
    public List<APIKeyResponse> listAPIKeysByApplicationIdAndUserId(String applicationId, String userId) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = application.fetchUserById(userId);
        List<APIKey> apiKeys = application.fetchAPIKeysByOwner(user);
        List<APIKeyResponse> responses = new ArrayList<>();
        for (APIKey apiKey: apiKeys) {
            responses.add(responseMapper.toResponse(apiKey));
        }
        return Collections.unmodifiableList(responses);
    }

    @Override
    public UserSessionResponse login(CredentialLoginRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserByIdentifier(request.getIdentifier());
        UserSession session = userAuthenticator.login(
                user, request.getPassword(), request.getSessionDetails(), request.getSessionTimeout());
        storeApplication(application);
        return responseMapper.toResponse(session);
    }

    @Override
    public UserSessionResponse login(OAuthLoginRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = application.fetchUserByOAuthIdentifier(request.getPlatform(), request.getIdentifier());
        UserSession session = userAuthenticator.login(
                user, request.getSessionDetails(), request.getSessionTimeout());
        storeApplication(application);
        return responseMapper.toResponse(session);
    }

    @Override
    public UserSessionResponse createUserSession(UserSessionCreateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        User user = getEnsuredUser(application, request.getUserId());
        UserSession session = application.createUserSession(
                user, request.getDetails(), request.getSessionTimeout());
        storeApplication(application);
        return responseMapper.toResponse(session);
    }

    @Override
    public UserSessionResponse getUserSessionByKey(String applicationId, String key) {
        Application application = getEnsuredApplicationById(applicationId);
        UserSession session = application.fetchUserSessionByKey(key);
        return responseMapper.toResponse(session);
    }

    @Override
    public void removeUserSessionByKey(String applicationId, String sessionKey) {
        Application application = getEnsuredApplicationById(applicationId);
        UserSession session = getEnsuredUserSession(application, sessionKey);
        application.removeUserSession(session);
        storeApplication(application);
    }

    private UserSession getEnsuredUserSession(Application application, String key) {
        UserSession session = application.fetchUserSessionByKey(key);
        if (null == session) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such user session found.")
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
        User user = application.fetchUserById(userId);
        application.removeAllUserSession(user);
    }

    @Override
    public PaginatedResult<List<UserSessionResponse>> listUserSessionsByUserId(String applicationId, String userId) {
        Application application = getEnsuredApplicationById(applicationId);
        User user = application.fetchUserById(userId);
        PaginatedResult<List<UserSession>> result = application.fetchUserSessionsByUser(user);
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
        Role role = application.addRole(request.getName());
        storeApplication(application);
        return responseMapper.toResponse(role);
    }

    @Override
    public RoleResponse updateRole(RoleUpdateRequest request) {
        Application application = getEnsuredApplicationById(request.getApplicationId());
        Role role = getEnsuredRole(application, request.getId());
        role.setName(request.getName());
        application.updateRole(role);
        storeApplication(application);
        return responseMapper.toResponse(role);
    }

    private Role getEnsuredRole(Application application, String id) {
        Role role = application.fetchRoleById(id);
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
        application.removeRole(role);
        storeApplication(application);
    }

    @Override
    public RoleResponse getRoleById(String applicationId, String id) {
        Application application = getEnsuredApplicationById(applicationId);
        Role role = application.fetchRoleById(id);
        return responseMapper.toResponse(role);
    }

    @Override
    public PaginatedResult<List<RoleResponse>> listRoles(String applicationId) {
        Application application = getEnsuredApplicationById(applicationId);
        PaginatedResult<List<Role>> result = application.fetchAllRoles();
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
        String[] keys = readParentKeyAndName(request.getKey());
        Resource parent = null;
        if (null != keys[0]) {
            parent = getEnsuredResource(application, keys[0]);
        }
        Resource resource = application.fetchResourceByKey(request.getKey());
        if (null == resource) {
            resource = application.createResource(keys[1], owner, parent);
            resource.setOwner(owner);
            storeApplication(application);
        }
        return responseMapper.toResponse(resource);
    }

    private Principal getEnsuredPrincipal(Application application, String id) {
        Principal principal = application.fetchPrincipalById(id);
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
        Resource resource = application.fetchResourceByKey(key);
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
        application.removeResource(resource);
        storeApplication(application);
    }

    @Override
    public ResourceResponse getResourceByKey(String applicationId, String key) {
        Application application = getEnsuredApplicationById(applicationId);
        Resource resource = application.fetchResourceByKey(key);
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
        PaginatedResult<List<Resource>> result = application.fetchResourcesByOwner(owner, parent);
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
        PaginatedResult<List<Resource>> result = application.fetchPermissionAssignedResources(principal);
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
        PaginatedResult<List<Resource>> result = application.fetchAllResources(parent);
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
        Resource resource = application.fetchResourceByKey(request.getResourceKey());
        Permission permission = resource.fetchPermissionById(principal);
        if (null == permission) {
            permission = resource.addPermission(principal, request.getActions());
        } else {
            permission.setActions(request.getActions());
            resource.updatePermission(permission);
        }
        application.updateResource(resource);
        storeApplication(application);
        return responseMapper.toResponse(permission);
    }

    @Override
    public void removePermission(String applicationId, String resourceKey, String principalId) {
        Application application = getEnsuredApplicationById(applicationId);
        Principal principal = getEnsuredPrincipal(application, principalId);
        Resource resource = getEnsuredResource(application, resourceKey);
        Permission permission = getEnsuredPermission(resource, principal);
        resource.removePermission(permission);
        application.updateResource(resource);
        storeApplication(application);
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
        Principal principal = application.fetchPrincipalById(principalId);
        Resource resource = application.fetchResourceByKey(resourceKey);
        Permission permission = resource.fetchPermissionById(principal);
        return responseMapper.toResponse(permission);
    }

    @Override
    public PaginatedResult<List<PermissionResponse>> listPermissions(
            String applicationId, String resourceKey) {
        Application application = getEnsuredApplicationById(applicationId);
        Resource resource = application.fetchResourceByKey(resourceKey);
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
        PermissionType result = resource.checkPermission(principal, request.getActionCode());
        return responseMapper.toResponse(principal, resource, request.getActionCode(), result);
    }
}
