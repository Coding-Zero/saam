package com.codingzero.saam.app;

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
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;


public interface SAAM {

    String requestOAuthAuthorizationUrl(OAuthAuthorizationUrlRequest request);

    OAuthAccessTokenResponse requestOAuthAccessToken(OAuthAccessTokenRequest request);

    IdentifierVerificationCodeResponse generateVerificationCode(
            IdentifierVerificationCodeGenerateRequest request);

    PasswordResetCodeResponse generateResetCode(PasswordResetCodeGenerateRequest request);

    /**Application**/

    ApplicationResponse addApplication(ApplicationAddRequest request);

    ApplicationResponse updateApplication(ApplicationUpdateRequest request);

    ApplicationResponse setPasswordPolicy(PasswordPolicySetRequest request);

    void removeApplication(String id);

    ApplicationResponse getApplicationById(String id);

    PaginatedResult<List<ApplicationResponse>> listApplications();

    ApplicationResponse addUsernamePolicy(UsernamePolicyAddRequest request);

    ApplicationResponse updateUsernamePolicy(UsernamePolicyUpdateRequest request);

    ApplicationResponse addEmailPolicy(EmailPolicyAddRequest request);

    ApplicationResponse updateEmailPolicy(EmailPolicyUpdateRequest request);

    ApplicationResponse removeIdentifierPolicy(String applicationId, IdentifierType type);

    ApplicationResponse addOAuthIdentifierPolicy(OAuthIdentifierPolicyAddRequest request);

    ApplicationResponse updateOAuthIdentifierPolicy(OAuthIdentifierPolicyUpdateRequest request);

    ApplicationResponse removeOAuthIdentifierPolicy(String applicationId, OAuthPlatform platform);

    /**User**/

    UserResponse register(UserRegisterRequest request);

    void removeUser(String applicationId, String id);

    UserResponse getUserById(String applicationId, String id);

    UserResponse getUserByIdentifier(String applicationId, String identifier);

    UserResponse getUserByOAuthIdentifier(String applicationId, OAuthPlatform platform, String identifier);

    PaginatedResult<List<UserResponse>> listUsersByApplicationId(String applicationId);

    UserResponse updateRoles(UserRoleUpdateRequest request);

    UserResponse changePassword(PasswordChangeRequest request);

    UserResponse resetPassword(PasswordResetRequest request);

    UserResponse addIdentifier(IdentifierAddRequest request);

    UserResponse removeIdentifier(IdentifierRemoveRequest request);

    UserResponse verifyIdentifier(IdentifierVerifyRequest request);

    UserResponse connectOAuthIdentifier(OAuthIdentifierConnectRequest request);

    UserResponse disconnectOAuthIdentifier(OAuthIdentifierDisconnectRequest request);

    /**APIKey**/

    APIKeyResponse addAPIKey(APIKeyAddRequest request);

    APIKeyResponse updateAPIKey(APIKeyUpdateRequest request);

    void verifyAPIKey(APIKeyVerifyRequest request);

    void removeAPIKeyById(String applicationId, String id);

    APIKeyResponse getAPIKeyById(String applicationId, String id);

    List<APIKeyResponse> listAPIKeysByApplicationIdAndUserId(String applicationId, String userId);

    /**UserSession**/

    UserSessionResponse login(CredentialLoginRequest request);

    UserSessionResponse login(OAuthLoginRequest request);

    UserSessionResponse createUserSession(UserSessionCreateRequest request);

    void removeUserSessionByKey(String applicationId, String sessionKey);

    void removeUserSessionsByUserId(String applicationId, String userId);

    UserSessionResponse getUserSessionByKey(String applicationId, String key);

    PaginatedResult<List<UserSessionResponse>> listUserSessionsByUserId(String applicationId, String userId);

    /**Role**/

    RoleResponse addRole(RoleAddRequest request);

    RoleResponse updateRole(RoleUpdateRequest request);

    void removeRole(String applicationId, String roleId);

    RoleResponse getRoleById(String applicationId, String id);

    PaginatedResult<List<RoleResponse>> listRoles(String applicationId);

    /**Resource**/

    ResourceResponse storeResource(ResourceStoreRequest request);

    void removeResource(String applicationId, String key);

    ResourceResponse getResourceByKey(String applicationId, String key);

    PaginatedResult<List<ResourceResponse>> getResourcesByOwnerId(
            String applicationId, String ownerId, String parentKey);

    PaginatedResult<List<ResourceResponse>> getGrantedResources(
            String applicationId, String principalId);

    PaginatedResult<List<ResourceResponse>> listResources(String applicationId, String parentKey);

    /**Permission**/

    PermissionResponse storePermission(PermissionStoreRequest request);

    void removePermission(String applicationId, String resourceKey, String principalId);

    PermissionResponse getPermissionByPrincipalId(
            String applicationId, String resourceKey, String principalId);

    PaginatedResult<List<PermissionResponse>> listPermissions(
            String applicationId, String resourceKey);

    PermissionCheckResponse checkPermission(PermissionCheckRequest request);

}
