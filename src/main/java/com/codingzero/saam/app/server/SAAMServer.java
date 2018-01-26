package com.codingzero.saam.app.server;

import com.codingzero.saam.app.APIKeyAddRequest;
import com.codingzero.saam.app.APIKeyResponse;
import com.codingzero.saam.app.APIKeyUpdateRequest;
import com.codingzero.saam.app.ApplicationAddRequest;
import com.codingzero.saam.app.ApplicationResponse;
import com.codingzero.saam.app.ApplicationUpdateRequest;
import com.codingzero.saam.app.CredentialLoginRequest;
import com.codingzero.saam.app.CredentialRegisterRequest;
import com.codingzero.saam.app.EmailPolicyAddRequest;
import com.codingzero.saam.app.EmailPolicyUpdateRequest;
import com.codingzero.saam.app.IdentifierAssignRequest;
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
import com.codingzero.saam.app.OAuthIdentifierUpdateRequest;
import com.codingzero.saam.app.OAuthLoginRequest;
import com.codingzero.saam.app.OAuthRegisterRequest;
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
import com.codingzero.saam.infrastructure.SSOAccessToken;
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

    private Application getCheckedApplicationById(String id) {
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
        Application application = getCheckedApplicationById(request.getId());
        application.setStatus(request.getStatus());
        application.setDescription(request.getDescription());
        application.setName(request.getName());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse updatePasswordPolicy(PasswordPolicyUpdateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        application.setPasswordPolicy(request.getPasswordPolicy());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public void removeApplication(String id) {
        Application application = getEnsuredApplication(id);
        removeApplication(application);
    }

    @Override
    public ApplicationResponse getApplicationById(String id) {
        Application application = getEnsuredApplication(id);
        return responseMapper.toResponse(application);
    }

    private Application getEnsuredApplication(String id) {
        Application application = getCheckedApplicationById(id);
        if (null == application) {
            throw BusinessError.noSuchEntityFound()
                    .message("No such application found.")
                    .details("applicationId", id)
                    .build();
        }
        return application;
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
        Application application = getCheckedApplicationById(request.getApplicationId());
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(request.getPlatform());
        return oAuthPlatformAgent.getAuthorizationUrl(
                request.getPlatform(), policy.getConfigurations(), request.getParameters());
    }

    @Override
    public OAuthAccessTokenResponse requestOAuthAccessToken(OAuthAccessTokenRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(request.getPlatform());
        SSOAccessToken token = oAuthPlatformAgent.requestAccessToken(
                policy.getPlatform(), policy.getConfigurations(), request.getParameters());
        return responseMapper.toResponse(application, token);
    }

    @Override
    public ApplicationResponse addUsernamePolicy(UsernamePolicyAddRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        UsernamePolicy policy = application.createUsernamePolicy(
        );
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse updateUsernamePolicy(UsernamePolicyUpdateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        UsernamePolicy policy = application.fetchUsernamePolicy();
        policy.setActive(request.isActive());
        application.updateIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse addEmailPolicy(EmailPolicyAddRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        application.createEmailPolicy(
                request.isVerificationRequired(), request.getDomains());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse updateEmailPolicy(EmailPolicyUpdateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        EmailPolicy policy = application.fetchEmailPolicy();
        policy.setActive(request.isActive());
        policy.setDomains(request.getDomains());
        policy.setVerificationRequired(request.isVerificationRequired());
        application.updateIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse removeIdentifierPolicy(String applicationId, IdentifierType type) {
        Application application = getCheckedApplicationById(applicationId);
        IdentifierPolicy policy = application.fetchIdentifierPolicy(type);
        application.removeIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse addOAuthIdentifierPolicy(OAuthIdentifierPolicyAddRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        OAuthIdentifierPolicy policy =
                application.createOAuthIdentifierPolicy(request.getPlatform(), request.getConfigurations());
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse updateOAuthIdentifierPolicy(OAuthIdentifierPolicyUpdateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(request.getPlatform());
        policy.setActive(request.isActive());
        policy.setConfigurations(request.getConfigurations());
        application.updateOAuthIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse removeOAuthIdentifierPolicy(String applicationId, OAuthPlatform platform) {
        Application application = getCheckedApplicationById(applicationId);
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(platform);
        application.removeOAuthIdentifierPolicy(policy);
        application = storeApplication(application);
        return responseMapper.toResponse(application);
    }

    @Override
    public UserResponse register(CredentialRegisterRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.createUser();
        user.changePassword(request.getPassword(), request.getPassword());
        user.setPlayingRoles(getRoles(application, request.getRoleIds()));
        application.updateUser(user);
        Map<IdentifierType, String> identifiers = request.getIdentifiers();
        for (Map.Entry<IdentifierType, String> entry: identifiers.entrySet()) {
            IdentifierPolicy policy = application.fetchIdentifierPolicy(entry.getKey());
            if (null == policy) {
                throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                        .message("No such identifier policy found, " + entry.getKey())
                        .details("entity", "IdentifierPolicy")
                        .details("type", entry.getKey())
                        .build();
            }
            policy.addIdentifier(entry.getValue(), user);
            application.updateIdentifierPolicy(policy);
        }
        storeApplication(application);
        return responseMapper.toResponse(user);
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
    public UserResponse register(OAuthRegisterRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.createUser();
        user.setPlayingRoles(getRoles(application, request.getRoleIds()));
        application.updateUser(user);
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(request.getPlatform());
        policy.addIdentifier(request.getIdentifier(), request.getProperties(), user);
        application.updateOAuthIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public void removeUser(String applicationId, String id) {
        Application application = getCheckedApplicationById(applicationId);
        User user = application.fetchUserById(id);
        application.removeUser(user);
        storeApplication(application);
    }

    @Override
    public UserResponse getUserById(String applicationId, String id) {
        Application application = getCheckedApplicationById(applicationId);
        User user = application.fetchUserById(id);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByIdentifier(String applicationId, String identifier) {
        Application application = getCheckedApplicationById(applicationId);
        User user = application.fetchUserByIdentifier(identifier);
        if (null == user) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No user found, " + identifier)
                    .details("type", "User")
                    .details("identifier", identifier)
                    .build();
        }
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByOAuthIdentifier(String applicationId, OAuthPlatform platform, String identifier) {
        Application application = getCheckedApplicationById(applicationId);
        User user = application.fetchUserByOAuthIdentifier(platform, identifier);
        if (null == user) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No user found, " + platform + "(" + identifier + ")")
                    .details("type", "User")
                    .details("platform", platform)
                    .details("identifier", identifier)
                    .build();
        }
        return responseMapper.toResponse(user);
    }

    @Override
    public PaginatedResult<List<UserResponse>> listUsersByApplicationId(String applicationId) {
        Application application = getCheckedApplicationById(applicationId);
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
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        user.setPlayingRoles(getRoles(application, request.getRoleIds()));
        application.updateUser(user);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse assignIdentifier(IdentifierAssignRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        IdentifierPolicy policy = application.fetchIdentifierPolicy(request.getType());
        policy.addIdentifier(request.getIdentifier(), user);
        application.updateIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse unassignIdentifier(IdentifierRemoveRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        IdentifierPolicy policy = application.fetchIdentifierPolicy(request.getType());
        Identifier identifier = policy.fetchIdentifierByUserAndId(user, request.getIdentifier());
        if (null == identifier) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such identifier found, " + request.getIdentifier() + " for user, " + user.getId())
                    .details("type", "Identifier")
                    .details("userId", user.getId())
                    .details("identifier", request.getIdentifier())
                    .build();
        }
        policy.removeIdentifier(identifier);
        application.updateIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public IdentifierVerificationCodeResponse generateVerificationCode(
            IdentifierVerificationCodeGenerateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        IdentifierPolicy policy = application.fetchIdentifierPolicy(request.getIdentifierType());
        Identifier identifier = policy.fetchIdentifierByUserAndId(user, request.getIdentifier());
        if (null == identifier) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such identifier found, " + request.getIdentifier() + " for user, " + user.getId())
                    .details("type", "Identifier")
                    .details("userId", user.getId())
                    .details("identifier", request.getIdentifier())
                    .build();
        }
        identifier.generateVerificationCode(request.getTimeout());
        policy.updateIdentifier(identifier);
        application.updateIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(identifier);
    }

    @Override
    public UserResponse verifyIdentifier(IdentifierVerifyRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        IdentifierPolicy policy = application.fetchIdentifierPolicy(request.getIdentifierType());
        Identifier identifier = policy.fetchIdentifierByUserAndId(user, request.getIdentifier());
        if (null == identifier) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such identifier found, " + request.getIdentifier() + " for user, " + user.getId())
                    .details("type", "Identifier")
                    .details("userId", user.getId())
                    .details("identifier", request.getIdentifier())
                    .build();
        }
        identifier.verify(request.getVerificationCode());
        policy.updateIdentifier(identifier);
        application.updateIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse connectOAuthIdentifier(OAuthIdentifierConnectRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(request.getPlatform());
        if (null == policy) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such oauth identifier policy found.")
                    .details("entity", OAuthIdentifierPolicy.class.getSimpleName())
                    .details("applicationId", application.getId())
                    .details("userId", user.getId())
                    .details("platform", request.getPlatform())
                    .details("identifier", request.getIdentifier())
                    .build();
        }
        policy.addIdentifier(request.getIdentifier(), request.getProperties(), user);
        application.updateOAuthIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse updateOAuthIdentifier(OAuthIdentifierUpdateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(request.getPlatform());
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
        identifier.setProperties(request.getProperties());
        policy.updateIdentifier(identifier);
        application.updateOAuthIdentifierPolicy(policy);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public UserResponse disconnectOAuthIdentifier(OAuthIdentifierDisconnectRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        OAuthIdentifierPolicy policy = application.fetchOAuthIdentifierPolicy(request.getPlatform());
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
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        user.changePassword(request.getOldPassword(), request.getNewPassword());
        application.updateUser(user);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public PasswordResetCodeResponse generateResetCode(PasswordResetCodeGenerateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        IdentifierPolicy policy = application.fetchIdentifierPolicy(request.getIdentifierType());
        User user = application.fetchUserById(request.getUserId());
        Identifier identifier = policy.fetchIdentifierByUserAndId(user, request.getIdentifier());
        if (null == identifier) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such identifier found, " + request.getIdentifier() + " for user, " + user.getId())
                    .details("type", "OAuthIdentifier")
                    .details("userId", user.getId())
                    .details("identifier", request.getIdentifier())
                    .build();
        }
        PasswordResetCode resetCode = user.generatePasswordResetCode(identifier, request.getTimeout());
        application.updateUser(user);
        storeApplication(application);
        return responseMapper.toResponse(user, identifier, resetCode);
    }

    @Override
    public UserResponse resetPassword(PasswordResetRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        user.resetPassword(request.getResetCode(), request.getNewPassword());
        application.updateUser(user);
        storeApplication(application);
        return responseMapper.toResponse(user);
    }

    @Override
    public APIKeyResponse addAPIKey(APIKeyAddRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        APIKey apiKey = application.createAPIKey(user, request.getName());
        storeApplication(application);
        return responseMapper.toResponse(apiKey);
    }

    @Override
    public APIKeyResponse updateAPIKey(APIKeyUpdateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        APIKey apiKey = getEnsuredAPIKey(application, request.getKey());
        apiKey.setName(request.getName());
        apiKey.setActive(request.isActive());
        application.updateAPIKey(apiKey);
        storeApplication(application);
        return responseMapper.toResponse(apiKey);
    }

    private APIKey getEnsuredAPIKey(Application application, String key) {
        APIKey apiKey = application.fetchAPIKeyByKey(key);
        if (null == apiKey) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such API key found.")
                    .details("entity", APIKey.class.getSimpleName())
                    .details("applicationId", application.getId())
                    .details("key", key)
                    .build();
        }
        return apiKey;
    }

    @Override
    public void removeAPIKey(String applicationId, String key) {
        Application application = getCheckedApplicationById(applicationId);
        APIKey apiKey = getEnsuredAPIKey(application, key);
        application.removeAPIKey(apiKey);
        storeApplication(application);
    }

    @Override
    public APIKeyResponse getAPIKeyByKey(String applicationId, String key) {
        Application application = getCheckedApplicationById(applicationId);
        APIKey apiKey = getEnsuredAPIKey(application, key);
        return responseMapper.toResponse(apiKey);
    }

    @Override
    public List<APIKeyResponse> listAPIKeysByApplicationIdAndUserId(String applicationId, String userId) {
        Application application = getCheckedApplicationById(applicationId);
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
        //TODO: This part of logic could be moved into domain layer.
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserByIdentifier(request.getIdentifier());
        UserSession session = userAuthenticator.login(
                user, request.getPassword(), request.getSessionDetails(), request.getSessionTimeout());
        storeApplication(application);
        return responseMapper.toResponse(session);
    }

    @Override
    public UserSessionResponse login(OAuthLoginRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserByOAuthIdentifier(request.getPlatform(), request.getIdentifier());
        UserSession session = userAuthenticator.login(
                user, request.getSessionDetails(), request.getSessionTimeout());
        storeApplication(application);
        return responseMapper.toResponse(session);
    }

    @Override
    public UserSessionResponse createUserSession(UserSessionCreateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        User user = application.fetchUserById(request.getUserId());
        UserSession session = application.createUserSession(
                user, request.getDetails(), request.getSessionTimeout());
        storeApplication(application);
        return responseMapper.toResponse(session);
    }

    @Override
    public UserSessionResponse getUserSessionByKey(String applicationId, String key) {
        Application application = getCheckedApplicationById(applicationId);
        UserSession session = application.fetchUserSessionByKey(key);
        return responseMapper.toResponse(session);
    }

    @Override
    public void cleanUserSession(String applicationId, String sessionKey) {
        Application application = getCheckedApplicationById(applicationId);
        UserSession session = application.fetchUserSessionByKey(sessionKey);
        application.removeUserSession(session);
        storeApplication(application);
    }

    @Override
    public void cleanAllUserSessions(String applicationId, String userId) {
        Application application = getCheckedApplicationById(applicationId);
        User user = application.fetchUserById(userId);
        application.removeAllUserSession(user);
    }

    @Override
    public PaginatedResult<List<UserSessionResponse>> listUserSessionsByUserId(String applicationId, String userId) {
        Application application = getCheckedApplicationById(applicationId);
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
        Application application = getCheckedApplicationById(request.getApplicationId());
        Role role = application.addRole(request.getName());
        storeApplication(application);
        return responseMapper.toResponse(role);
    }

    @Override
    public RoleResponse updateRole(RoleUpdateRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        Role role = application.fetchRoleById(request.getId());
        role.setName(request.getName());
        application.updateRole(role);
        storeApplication(application);
        return responseMapper.toResponse(role);
    }

    @Override
    public void removeRole(String applicationId, String roleId) {
        Application application = getCheckedApplicationById(applicationId);
        Role role = application.fetchRoleById(roleId);
        application.removeRole(role);
        storeApplication(application);
    }

    @Override
    public RoleResponse getRoleById(String applicationId, String id) {
        Application application = getCheckedApplicationById(applicationId);
        Role role = application.fetchRoleById(id);
        return responseMapper.toResponse(role);
    }

    @Override
    public PaginatedResult<List<RoleResponse>> listRoles(String applicationId) {
        Application application = getCheckedApplicationById(applicationId);
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
        Application application = getCheckedApplicationById(request.getApplicationId());
        Principal owner = application.fetchPrincipalById(request.getUserId());
        String[] keys = readParentKeyAndName(request.getKey());
        Resource parent = null;
        if (null != keys[0]) {
            parent = application.fetchResourceByKey(keys[0]);
            if (null == parent) {
                throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                        .message("No such parent resource found, " + keys[0])
                        .details("type", "Resource")
                        .details("userId", request.getUserId())
                        .details("key", keys[0])
                        .build();
            }
        }
        Resource resource = application.fetchResourceByKey(request.getKey());
        if (null == resource) {
            resource = application.createResource(keys[1], owner, parent);
            resource.setOwner(owner);
            storeApplication(application);
        }
        return responseMapper.toResponse(resource);
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

    @Override
    public void removeResource(String applicationId, String key) {
        Application application = getCheckedApplicationById(applicationId);
        Resource resource = application.fetchResourceByKey(key);
        application.removeResource(resource);
        storeApplication(application);
    }

    @Override
    public ResourceResponse getResourceByKey(String applicationId, String key) {
        Application application = getCheckedApplicationById(applicationId);
        Resource resource = application.fetchResourceByKey(key);
        return responseMapper.toResponse(resource);
    }

    @Override
    public PaginatedResult<List<ResourceResponse>> getResourcesByOwnerId(
            String applicationId, String ownerId, String parentKey) {
        Application application = getCheckedApplicationById(applicationId);
        Principal owner = application.fetchPrincipalById(ownerId);
        Resource parent = null;
        if (null != parentKey && parentKey.trim().length() > 0) {
            parent = application.fetchResourceByKey(parentKey);
            if (null == parent) {
                throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                        .message("No such resource found for the given key")
                        .details("entity", Resource.class.getSimpleName())
                        .details("applicationId", application.getId())
                        .details("key", parentKey)
                        .build();
            }
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
            String applicationId, String principalId, String parentKey) {
        Application application = getCheckedApplicationById(applicationId);
        Principal principal = application.fetchPrincipalById(principalId);
        Resource parent = null;
        if (null != parentKey && parentKey.trim().length() > 0) {
            parent = application.fetchResourceByKey(parentKey);
            if (null == parent) {
                throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                        .message("No such parent resource found, " + parentKey)
                        .details("type", "Resource")
                        .details("key", parentKey)
                        .build();
            }
        }
        PaginatedResult<List<Resource>> result = application.fetchPermissionAssignedResources(principal, parent);
        return new PaginatedResult<>(new PaginatedResultMapper<List<ResourceResponse>, List<Resource>>() {
            @Override
            protected List<ResourceResponse> toResult(List<Resource> source, Object[] arguments) {
                return toResourceResponses(source);
            }
        }, result);
    }

    @Override
    public PaginatedResult<List<ResourceResponse>> listResources(String applicationId, String parentKey) {
        Application application = getCheckedApplicationById(applicationId);
        Resource parent = null;
        if (null != parentKey && parentKey.trim().length() > 0) {
            parent = application.fetchResourceByKey(parentKey);
            if (null == parent) {
                throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                        .message("No such parent resource found, " + parentKey)
                        .details("type", "Resource")
                        .details("key", parentKey)
                        .build();
            }
        }
        PaginatedResult<List<Resource>> result = application.fetchAllResources(parent);
        return new PaginatedResult<>(new PaginatedResultMapper<List<ResourceResponse>, List<Resource>>() {
            @Override
            protected List<ResourceResponse> toResult(List<Resource> source, Object[] arguments) {
                return toResourceResponses(source);
            }
        }, result);
    }

//    @Override
//    public ActionResponse addAction(ActionAddRequest request) {
//        Application application = getCheckedApplicationById(request.getApplicationId());
//        Resource resource = null;
//        if (null != request.getResourceKey()) {
//            resource = application.fetchResourceByKey(request.getResourceKey());
//            if (null == resource) {
//                throw new NoSuchEntityFoundException("No such resource found, " + request.getResourceKey());
//            }
//        }
//        Action action = application.createAction(request.getCode(), request.getName(), resource);
//        storeApplication(application);
//        return responseMapper.toResponse(action);
//    }
//
//    @Override
//    public ActionResponse updateAction(ActionUpdateRequest request) {
//        Application application = getCheckedApplicationById(request.getApplicationId());
//        Action action = application.fetchAction(request.getCode());
//        action.setName(request.getName());
//        application.updateAction(action);
//        storeApplication(application);
//        return responseMapper.toResponse(action);
//    }
//
//    @Override
//    public void removeAction(String applicationId, String code) {
//        Application application = getCheckedApplicationById(applicationId);
//        Action action = application.fetchAction(code);
//        application.removeAction(action);
//        storeApplication(application);
//    }
//
//    @Override
//    public ActionResponse getActionByCode(String applicationId, String code) {
//        Application application = getCheckedApplicationById(applicationId);
//        Action action = application.fetchAction(code);
//        return responseMapper.toResponse(action);
//    }
//
//    @Override
//    public PaginatedResult<List<ActionResponse>> getActionsByResourceKey(String applicationId, String resourceKey) {
//        Application application = getCheckedApplicationById(applicationId);
//        Resource resource = application.fetchResourceByKey(resourceKey);
//        if (null == resource) {
//            throw new NoSuchEntityFoundException("No such resource found, " + resourceKey);
//        }
//        PaginatedResult<List<Action>> result = application.fetchActionsByResource(resource);
//        return new PaginatedResult<>(actionResponsePageMapper, "getActionsByResource", result);
//    }
//
//    @Override
//    public PaginatedResult<List<ActionResponse>> listActions(String applicationId) {
//        Application application = getCheckedApplicationById(applicationId);
//        PaginatedResult<List<Action>> result = application.fetchAllActions();
//        return new PaginatedResult<>(actionResponsePageMapper, "listActions", result);
//    }

    @Override
    public PermissionResponse storePermission(PermissionStoreRequest request) {
        Application application = getCheckedApplicationById(request.getApplicationId());
        Principal principal = application.fetchPrincipalById(request.getPrincipalId());
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
        Application application = getCheckedApplicationById(applicationId);
        Principal principal = application.fetchPrincipalById(principalId);
        Resource resource = application.fetchResourceByKey(resourceKey);
        Permission permission = resource.fetchPermissionById(principal);
        resource.removePermission(permission);
        application.updateResource(resource);
        storeApplication(application);
    }

    @Override
    public PermissionResponse getPermissionByPrincipalId(
            String applicationId, String resourceKey, String principalId) {
        Application application = getCheckedApplicationById(applicationId);
        Principal principal = application.fetchPrincipalById(principalId);
        Resource resource = application.fetchResourceByKey(resourceKey);
        Permission permission = resource.fetchPermissionById(principal);
        return responseMapper.toResponse(permission);
    }

//    @Override
//    public PaginatedResult<List<PermissionResponse>> getPermissionsByPrincipalId(
//            String applicationId, String principalId) {
//        Application application = getCheckedApplicationById(applicationId);
//        Principal principal = application.fetchPrincipalById(principalId);
//        PaginatedResult<List<Permission>> result = application.fetchPermissionsByPrincipal(principal);
//        return new PaginatedResult<>(permissionResponsePageMapper, "getPermissionByPrincipalId", result);
//    }

    @Override
    public PaginatedResult<List<PermissionResponse>> listPermissions(
            String applicationId, String resourceKey) {
        Application application = getCheckedApplicationById(applicationId);
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
        Application application = getCheckedApplicationById(request.getApplicationId());
        if (null == application) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such application found, " + request.getApplicationId())
                    .details("type", "Application")
                    .details("id", request.getApplicationId())
                    .build();
        }
        Principal principal = application.fetchPrincipalById(request.getPrincipalId());
        if (null == principal) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such principal found, " + request.getPrincipalId())
                    .details("type", "Principal")
                    .details("id", request.getPrincipalId())
                    .build();
        }
        Resource resource = application.fetchResourceByKey(request.getResourceKey());
        if (null == resource) {
            throw BusinessError.raise(BusinessError.DefaultErrors.NO_SUCH_ENTITY_FOUND)
                    .message("No such resource found, " + request.getResourceKey())
                    .details("type", "Resource")
                    .details("key", request.getResourceKey())
                    .build();
        }
        PermissionType result = resource.checkPermission(principal, request.getActionCode());
        return responseMapper.toResponse(principal, resource, request.getActionCode(), result);
    }
}
