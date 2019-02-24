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
import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.common.PermissionType;
import com.codingzero.utilities.error.BusinessError;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class SAAMTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SAAM saam;
    private List<ApplicationResponse> generatedApplications;

    @Before
    public void setUp() {
        saam = getSAAM();
        generatedApplications = new LinkedList<>();
    }

    @After
    public void clean() {
        for (ApplicationResponse response: generatedApplications) {
            try {
                saam.removeApplication(response.getId());
            } catch (Exception e) {
                //do nothing.
            }
        }
    }

    @Test
    public void testRequestOAuthAuthorizationUrl() {
        ApplicationResponse app = createApplication();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("state", "state-123456");
        parameters.put("redirect-url", "http://localhost:8080");
        String url = saam.requestOAuthAuthorizationUrl(
                new OAuthAuthorizationUrlRequest(app.getId(), OAuthPlatform.GOOGLE, parameters));

        assertNotNull(url);
    }

    @Test
    public void testRequestOAuthAuthorizationUrl_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.requestOAuthAuthorizationUrl(
                new OAuthAuthorizationUrlRequest(app.getId(), OAuthPlatform.GOOGLE, Collections.emptyMap()));
    }

    @Test
    public void testRequestOAuthAuthorizationUrl_WithoutPolicy() {
        ApplicationResponse app = createSimpleApplication();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("state", "state-123456");
        parameters.put("redirect-url", "http://localhost:8080");

        thrown.expect(BusinessError.class);
        saam.requestOAuthAuthorizationUrl(
                new OAuthAuthorizationUrlRequest(app.getId(), OAuthPlatform.GOOGLE, parameters));
    }

    @Test
    public void testRequestOAuthAccessToken() {
        ApplicationResponse app = createApplication();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key1", "value1");
        OAuthAccessTokenResponse response = saam.requestOAuthAccessToken(
                new OAuthAccessTokenRequest(app.getId(), OAuthPlatform.GOOGLE, parameters));

        assertNotNull(response);
        assertEquals(app.getId(), response.getApplicationId());
        assertEquals(OAuthPlatform.GOOGLE, response.getPlatform());
    }

    @Test
    public void testRequestOAuthAccessToken_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.requestOAuthAccessToken(
                new OAuthAccessTokenRequest(app.getId(), OAuthPlatform.GOOGLE, Collections.emptyMap()));
    }

    @Test
    public void testRequestOAuthAccessToken_WithoutPolicy() {
        ApplicationResponse app = createSimpleApplication();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("key1", "value1");

        thrown.expect(BusinessError.class);
        saam.requestOAuthAccessToken(
                new OAuthAccessTokenRequest(app.getId(), OAuthPlatform.GOOGLE, parameters));
    }

    @Test
    public void testGenerateVerificationCode() {
        ApplicationResponse app = createApplication();

        UserResponse user = registerUser(app.getId());
        String email = getIdentifier(user, IdentifierType.EMAIL).getContent();

        long timestamp = System.currentTimeMillis();
        IdentifierVerificationCodeResponse response = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), email, 1000));

        assertNotNull(response);
        assertEquals(app.getId(), response.getApplicationId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(IdentifierType.EMAIL, response.getIdentifierType());
        assertEquals(email, response.getIdentifier());
        assertNotNull(response.getCode());
        assertTrue((response.getExpirationTime().getTime() - timestamp >= 1000));
    }

    @Test
    public void testGenerateVerificationCode_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String email = getIdentifier(user, IdentifierType.EMAIL).getContent();

        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), email, 1000));
    }

    @Test
    public void testGenerateVerificationCode_WithoutPolicy() {
        ApplicationResponse app = createSimpleApplication();

        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        thrown.expect(BusinessError.class);
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), "foo@foo.com", 1000));
    }

    @Test
    public void testGenerateVerificationCode_NoIdentifierFound() {
        ApplicationResponse app = createApplication();

        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        thrown.expect(BusinessError.class);
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), "foo@foo.com", 1000));
    }

    @Test
    public void testGenerateVerificationCode_VerificationIsNotRequired() {
        ApplicationResponse app = createApplication();

        UserResponse user = registerUser(app.getId());
        String username = getIdentifier(user, IdentifierType.USERNAME).getContent();

        thrown.expect(BusinessError.class);
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), username, 1000));
    }

    @Test
    public void testGenerateResetCode() {
        ApplicationResponse app = createApplication(false);

        UserResponse user = registerUser(app.getId());

        String email = getIdentifier(user, IdentifierType.EMAIL).getContent();
        long timestamp = System.currentTimeMillis();
        PasswordResetCodeResponse response = saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email, 1000));

        assertNotNull(response);
        assertEquals(app.getId(), response.getApplicationId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(IdentifierType.EMAIL, response.getIdentifierType());
        assertEquals(email, response.getIdentifier());
        assertNotNull(response.getCode());
        assertTrue((response.getExpirationTime().getTime() - timestamp >= 1000));
    }

    @Test
    public void testGenerateResetCode_InactiveApplication() {
        ApplicationResponse app = createApplication(false);
        UserResponse user = registerUser(app.getId());
        String email = getIdentifier(user, IdentifierType.EMAIL).getContent();

        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email, 1000));
    }

    @Test
    public void testGenerateResetCode_WithoutPolicy() {
        ApplicationResponse app = createSimpleApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        thrown.expect(BusinessError.class);
        saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, "foo@foo.com", 1000));
    }

    @Test
    public void testGenerateResetCode_NoIdentifierFound() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        thrown.expect(BusinessError.class);
        saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, "foo@foo.com", 1000));
    }

    @Test
    public void testAddApplication() {
        ApplicationResponse app = createApplication();
        assertNotNull(app);
    }

    @Test
    public void testAddApplication_DuplicateName() {
        String name = getApplicationName();
        createApplication(name, true);
        thrown.expect(BusinessError.class);
        createApplication(name, true);
    }

    @Test
    public void testUpdateApplication() {
        ApplicationResponse app = createApplication();
        String newName = getApplicationName();
        String newDesc = "new description";
        ApplicationUpdateRequest request =
                new ApplicationUpdateRequest(
                        app.getId(),
                        newName,
                        newDesc,
                        ApplicationStatus.ACTIVE);
        ApplicationResponse actualApp = saam.updateApplication(request);
        assertEquals(app.getId(), actualApp.getId());
        assertEquals(newName, actualApp.getName());
        assertEquals(newDesc, actualApp.getDescription());
        assertEquals(app.getCreationTime(), actualApp.getCreationTime());
        assertEquals(ApplicationStatus.ACTIVE, actualApp.getStatus());
    }

    @Test
    public void testUpdateApplication_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        String newName = getApplicationName();
        String newDesc = "new description";
        ApplicationResponse actualApp = saam.updateApplication(
                new ApplicationUpdateRequest(app.getId(), newName, newDesc, ApplicationStatus.ACTIVE));
        assertEquals(app.getId(), actualApp.getId());
        assertEquals(newName, actualApp.getName());
        assertEquals(newDesc, actualApp.getDescription());
        assertEquals(app.getCreationTime(), actualApp.getCreationTime());
        assertEquals(ApplicationStatus.ACTIVE, actualApp.getStatus());
    }

    @Test
    public void testUpdateApplication_DuplicateName() {
        ApplicationResponse app1 = createApplication();
        ApplicationResponse app2 = createApplication();
        String newName = app1.getName();
        String newDesc = "new description";
        ApplicationUpdateRequest request =
                new ApplicationUpdateRequest(
                        app2.getId(),
                        newName,
                        newDesc,
                        ApplicationStatus.DEACTIVE);
        thrown.expect(BusinessError.class);
        saam.updateApplication(request);
    }

    @Test
    public void testSetPasswordPolicy() {
        ApplicationResponse app = createApplication();
        PasswordPolicy policy = app.getPasswordPolicy();
        app = saam.setPasswordPolicy(new PasswordPolicySetRequest(
                app.getId(), new PasswordPolicy(8, 50, false, false)));
        PasswordPolicy actualPolicy = app.getPasswordPolicy();
        assertNotEquals(policy.getMinLength(), actualPolicy.getMinLength());
        assertNotEquals(policy.getMaxLength(), actualPolicy.getMaxLength());
        assertNotEquals(policy.isNeedCapital(), actualPolicy.isNeedCapital());
        assertNotEquals(policy.isNeedSpecialChar(), actualPolicy.isNeedSpecialChar());
    }

    @Test
    public void testSetPasswordPolicy_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.setPasswordPolicy(new PasswordPolicySetRequest(
                app.getId(), new PasswordPolicy(8, 50, false, false)));
    }

    @Test
    public void testSetPasswordPolicy_NullValue() {
        ApplicationResponse app = createApplication();
        app = saam.setPasswordPolicy(new PasswordPolicySetRequest(
                app.getId(), null));
        PasswordPolicy actualPolicy = app.getPasswordPolicy();
        assertNull(actualPolicy);
    }

    @Test
    public void testRemoveApplication() {
        ApplicationResponse app = createApplication();
        saam.removeApplication(app.getId());
        ApplicationResponse actualApp = saam.getApplicationById(app.getId());
        assertNull(actualApp);
    }

    @Test
    public void testRemoveApplication_NotExist() {
        ApplicationResponse app = createApplication();
        saam.removeApplication(app.getId());
        thrown.expect(BusinessError.class);
        saam.removeApplication(app.getId());
    }

    @Test
    public void testListApplications() {
        List<ApplicationResponse> apps = createApplications(3);
        PaginatedResult<List<ApplicationResponse>> actualResult = saam.listApplications();
        List<ApplicationResponse> actualApps = actualResult.start(new OffsetBasedResultPage(1, 3)).getResult();

        assertEquals(3, actualApps.size());
        for (ApplicationResponse app: apps) {
            for (ApplicationResponse actualApp: actualApps) {
                if (app.getId().equalsIgnoreCase(actualApp.getId())) {
                    assertApplication(app, actualApp);
                }
            }
        }
    }

    @Test
    public void testListApplications_MoreThanOnePage() {
        List<ApplicationResponse> apps = createApplications(10);
        PaginatedResult<List<ApplicationResponse>> actualResult = saam.listApplications();
        List<ApplicationResponse> actualApps = actualResult.start(new OffsetBasedResultPage(1, 3)).getResult();
        int total = 0;
        do {
            for (ApplicationResponse actualApp: actualApps) {
                for (ApplicationResponse app: apps) {
                    if (app.getId().equalsIgnoreCase(actualApp.getId())) {
                        assertApplication(app, actualApp);
                    }
                }
            }
            total += actualApps.size();
            actualApps = actualResult.next().getResult();
        } while (actualApps.size() > 0);
        assertEquals(10, total);
    }

    @Test
    public void testAddUsernamePolicy_InactiveApplication() {
        ApplicationResponse app = createSimpleApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.addUsernamePolicy(
                new UsernamePolicyAddRequest(app.getId()));
    }

    @Test
    public void testAddUsernamePolicy_Duplicate() {
        ApplicationResponse app = createApplication();
        thrown.expect(BusinessError.class);
        saam.addUsernamePolicy(
                new UsernamePolicyAddRequest(app.getId()));
    }

    @Test
    public void testUpdateUsernamePolicy() {
        ApplicationResponse app = createApplication();
        ApplicationResponse.UsernamePolicy oldPolicy = app.getUsernamePolicy();
        app = saam.updateUsernamePolicy(new UsernamePolicyUpdateRequest(app.getId(), false));
        ApplicationResponse.UsernamePolicy actualPolicy = app.getUsernamePolicy();
        assertEquals(oldPolicy.getCreationTime(), actualPolicy.getCreationTime());
        assertEquals(oldPolicy.getMaxLength(), actualPolicy.getMaxLength());
        assertEquals(oldPolicy.getMinLength(), actualPolicy.getMinLength());
        assertEquals(oldPolicy.getFormat(), actualPolicy.getFormat());
        assertNotEquals(oldPolicy.getUpdateTime(), actualPolicy.getUpdateTime());
        assertNotEquals(oldPolicy.isActive(), actualPolicy.isActive());
    }

    @Test
    public void testUpdateUsernamePolicy_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.updateUsernamePolicy(new UsernamePolicyUpdateRequest(app.getId(), false));
    }

    @Test
    public void testUpdateUsernamePolicy_NotExist() {
        ApplicationResponse app = createSimpleApplication();

        thrown.expect(BusinessError.class);
        saam.updateUsernamePolicy(new UsernamePolicyUpdateRequest(app.getId(), false));
    }

    @Test
    public void testAddEmailPolicy_InactiveApplication() {
        ApplicationResponse app = createSimpleApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.addEmailPolicy(
                new EmailPolicyAddRequest(app.getId(), false, Arrays.asList("foo.com")));
    }

    @Test
    public void testAddEmailPolicy_Duplicate() {
        ApplicationResponse app = createApplication();
        thrown.expect(BusinessError.class);
        saam.addEmailPolicy(
                new EmailPolicyAddRequest(app.getId(), false, Arrays.asList("foo.com")));
    }

    @Test
    public void testUpdateEmailPolicy() {
        ApplicationResponse app = createApplication();
        ApplicationResponse.EmailPolicy oldPolicy = app.getEmailPolicy();
        app = saam.updateEmailPolicy(
                new EmailPolicyUpdateRequest(
                        app.getId(), false, Arrays.asList("foo.com"), false));
        ApplicationResponse.EmailPolicy actualPolicy = app.getEmailPolicy();
        assertEquals(oldPolicy.getType(), actualPolicy.getType());
        assertNotEquals(oldPolicy.isVerificationRequired(), actualPolicy.isVerificationRequired());
        assertEquals(oldPolicy.getMinLength(), actualPolicy.getMinLength());
        assertEquals(oldPolicy.getMaxLength(), actualPolicy.getMaxLength());
        assertNotEquals(oldPolicy.isActive(), actualPolicy.isActive());
        assertEquals(oldPolicy.getCreationTime(), actualPolicy.getCreationTime());
        assertNotEquals(oldPolicy.getUpdateTime(), actualPolicy.getUpdateTime());
        assertNotEquals(oldPolicy.getDomains(), actualPolicy.getDomains());
    }

    @Test
    public void testUpdateEmailPolicy_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.updateEmailPolicy(
                new EmailPolicyUpdateRequest(
                        app.getId(), false, Arrays.asList("foo.com"), false));
    }

    @Test
    public void testUpdateEmailPolicy_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.updateEmailPolicy(
                new EmailPolicyUpdateRequest(
                        app.getId(), false, Arrays.asList("foo.com"), false));
    }

    @Test
    public void testRemoveUsernamePolicy() {
        ApplicationResponse app = createApplication();

        app = saam.removeIdentifierPolicy(app.getId(), IdentifierType.USERNAME);
        assertNull(app.getUsernamePolicy());
    }

    @Test
    public void testRemoveUsernamePolicy_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeIdentifierPolicy(app.getId(), IdentifierType.USERNAME);
    }

    @Test
    public void testRemoveUsernamePolicy_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.removeIdentifierPolicy(app.getId(), IdentifierType.USERNAME);
    }

    @Test
    public void testRemoveEmailPolicy() {
        ApplicationResponse app = createApplication();

        app = saam.removeIdentifierPolicy(app.getId(), IdentifierType.EMAIL);
        assertNull(app.getEmailPolicy());
    }

    @Test
    public void testRemoveEmailPolicy_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeIdentifierPolicy(app.getId(), IdentifierType.EMAIL);
    }

    @Test
    public void testRemoveEmailPolicy_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.removeIdentifierPolicy(app.getId(), IdentifierType.EMAIL);
    }

    @Test
    public void testAddOAuthIdentifierPolicy_InactiveApplication() {
        ApplicationResponse app = createSimpleApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.addOAuthIdentifierPolicy(
                new OAuthIdentifierPolicyAddRequest(app.getId(), OAuthPlatform.GOOGLE, Collections.emptyMap()));
    }

    @Test
    public void testAddOAuthIdentifierPolicy_Duplicate() {
        ApplicationResponse app = createApplication();
        thrown.expect(BusinessError.class);
        saam.addOAuthIdentifierPolicy(
                new OAuthIdentifierPolicyAddRequest(app.getId(), OAuthPlatform.GOOGLE, Collections.emptyMap()));
    }

    @Test
    public void testUpdateOAuthIdentifierPolicy() {
        ApplicationResponse app = createApplication();
        List<ApplicationResponse.OAuthIdentifierPolicy> oldPolicies = app.getOAuthIdentifierPolicies();
        for (ApplicationResponse.OAuthIdentifierPolicy oldPolicy: oldPolicies) {
            Map<String, Object> configurations = new HashMap<>();
            configurations.put("key1", "value1");
            app = saam.updateOAuthIdentifierPolicy(
                    new OAuthIdentifierPolicyUpdateRequest(
                            app.getId(), oldPolicy.getPlatform(), configurations, false));
        }
        List<ApplicationResponse.OAuthIdentifierPolicy> actualPolicies = app.getOAuthIdentifierPolicies();
        assertEquals(oldPolicies.size(), actualPolicies.size());
        for (ApplicationResponse.OAuthIdentifierPolicy oldPolicy: oldPolicies) {
            for (ApplicationResponse.OAuthIdentifierPolicy actualPolicy: actualPolicies) {
                if (oldPolicy.getPlatform() == actualPolicy.getPlatform()) {
                    assertEquals(oldPolicy.getPlatform(), actualPolicy.getPlatform());
                    assertNotEquals(oldPolicy.getConfigurations(), actualPolicy.getConfigurations());
                    assertNotEquals(oldPolicy.isActive(), actualPolicy.isActive());
                }
            }
        }
    }

    @Test
    public void testUpdateOAuthIdentifierPolicy_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        Map<String, Object> configurations = new HashMap<>();
        configurations.put("key1", "value1");
        saam.updateOAuthIdentifierPolicy(
                new OAuthIdentifierPolicyUpdateRequest(
                        app.getId(), OAuthPlatform.GOOGLE, configurations, false));
    }

    @Test
    public void testUpdateOAuthIdentifierPolicy_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.updateOAuthIdentifierPolicy(
                new OAuthIdentifierPolicyUpdateRequest(
                        app.getId(), OAuthPlatform.GOOGLE, Collections.emptyMap(), false));
    }

    @Test
    public void testRemoveOAuthIdentifierPolicy() {
        ApplicationResponse app = createApplication();
        List<ApplicationResponse.OAuthIdentifierPolicy> oldPolicies = app.getOAuthIdentifierPolicies();
        for (ApplicationResponse.OAuthIdentifierPolicy oldPolicy: oldPolicies) {
            app = saam.removeOAuthIdentifierPolicy(app.getId(), oldPolicy.getPlatform());
        }
        assertEquals(0, app.getOAuthIdentifierPolicies().size());
    }

    @Test
    public void testRemoveOAuthIdentifierPolicy_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeOAuthIdentifierPolicy(app.getId(), OAuthPlatform.GOOGLE);
    }

    @Test
    public void testRemoveOAuthIdentifierPolicy_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.removeOAuthIdentifierPolicy(app.getId(), OAuthPlatform.GOOGLE);
    }

    @Test
    public void testCreateUser() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        assertNotNull(user);
        assertEquals(app.getId(), user.getApplicationId());
        assertEquals(2, user.getIdentifiers().size());
        assertEquals(1, user.getOAuthIdentifiers().size());
        assertEquals(0, user.getRoles().size());
    }

    @Test
    public void testCreateUser_InactiveApplication() {
        ApplicationResponse app = createApplication();
        app = updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        registerUser(app.getId());
    }

    @Test
    public void testCreateUser_Plain() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(app.getId()));
        assertNotNull(user);
        assertEquals(app.getId(), user.getApplicationId());
        assertEquals(0, user.getIdentifiers().size());
        assertEquals(0, user.getOAuthIdentifiers().size());
        assertEquals(0, user.getRoles().size());
    }

    @Test
    public void testRemoveUser() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        saam.removeUser(user.getApplicationId(), user.getId());
        UserResponse actualUser = saam.getUserById(user.getApplicationId(), user.getId());
        assertNull(actualUser);

        List<UserResponse.Identifier> identifiers = user.getIdentifiers();
        Map<IdentifierType, String> newIdentifiers = new HashMap<>();
        for (UserResponse.Identifier identifier: identifiers) {
            newIdentifiers.put(identifier.getType(), identifier.getContent());
        }

        List<UserResponse.OAuthIdentifier> oAuthIdentifiers = user.getOAuthIdentifiers();
        Map<OAuthPlatform, UserRegisterRequest.OAuthIdentifier> newOAuthIdentifiers = new HashMap<>();
        for (UserResponse.OAuthIdentifier identifier: oAuthIdentifiers) {
            newOAuthIdentifiers.put(
                    identifier.getPlatform(),
                    new UserRegisterRequest.OAuthIdentifier(identifier.getContent(), identifier.getProperties()));
        }

        registerUser(app.getId(), newIdentifiers, newOAuthIdentifiers);
    }

    @Test
    public void testRemoveUser_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeUser(user.getApplicationId(), user.getId());
    }

    @Test
    public void testRemoveUser_NotExistUser() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        saam.removeUser(user.getApplicationId(), user.getId());
        thrown.expect(BusinessError.class);
        saam.removeUser(user.getApplicationId(), user.getId());
    }

    @Test
    public void testGetUserByIdentifier() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String username = getIdentifier(user, IdentifierType.USERNAME).getContent();
        UserResponse actualUser = saam.getUserByIdentifier(user.getApplicationId(), username);
        assertUser(user, actualUser);
    }

    @Test
    public void testGetUserByIdentifier_NotExist() {
        ApplicationResponse app = createApplication();
        String username = "username";
        UserResponse actualUser = saam.getUserByIdentifier(app.getId(), username);
        assertNull(actualUser);
    }

    @Test
    public void testGetUserByOAuthIdentifier() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String identifier = getOAuthIdentifier(user, OAuthPlatform.GOOGLE).getContent();
        UserResponse actualUser = saam.getUserByOAuthIdentifier(
                user.getApplicationId(), OAuthPlatform.GOOGLE, identifier);
        assertUser(user, actualUser);
    }

    @Test
    public void testGetUserByOAuthIdentifier_NotExist() {
        ApplicationResponse app = createApplication();
        String identifier = "google-oauth-idenfier";
        UserResponse actualUser = saam.getUserByOAuthIdentifier(
                app.getId(), OAuthPlatform.GOOGLE, identifier);
        assertNull(actualUser);
    }

    @Test
    public void testListUsersByApplicationId() {
        ApplicationResponse app1 = createApplication();
        List<UserResponse> users1 = registerUsers(app1.getId(), 3);
        ApplicationResponse app2 = createApplication();
        registerUsers(app2.getId(), 5);

        PaginatedResult<List<UserResponse>> actualResult = saam.listUsersByApplicationId(app1.getId());
        List<UserResponse> actualUsers = actualResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(3, actualUsers.size());
        for (UserResponse user: users1) {
            for (UserResponse actualUser: actualUsers) {
                if (user.getId().equals(actualUser.getId())) {
                    assertUser(user, actualUser);
                }
            }
        }
    }

    @Test
    public void testUpdateRoles() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String roleName = getRoleName();
        RoleResponse role = createRole(app.getId(), roleName);
        UserResponse actualUser = saam.updateRoles(
                new UserRoleUpdateRequest(app.getId(), user.getId(), Arrays.asList(role.getId())));
        assertEquals(1, actualUser.getRoles().size());
        assertEquals(roleName, actualUser.getRoles().get(0).getName());
    }

    @Test
    public void testUpdateRoles_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String roleName = getRoleName();
        RoleResponse role = createRole(app.getId(), roleName);
        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.updateRoles(
                new UserRoleUpdateRequest(app.getId(), user.getId(), Arrays.asList(role.getId())));
    }

    @Test
    public void testUpdateRoles_NotExist() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String roleName = getRoleName();
        RoleResponse role = createRole(app.getId(), roleName);
        saam.removeRole(app.getId(), role.getId());
        thrown.expect(BusinessError.class);
        saam.updateRoles(new UserRoleUpdateRequest(app.getId(), user.getId(), Arrays.asList(role.getId())));
    }

    @Test
    public void testChangePassword() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String oldPassword = "Password!";
        String newPassword = "Password!1";
        saam.changePassword(
                new PasswordChangeRequest(app.getId(), user.getId(), oldPassword, newPassword));
        saam.changePassword(
                new PasswordChangeRequest(app.getId(), user.getId(), newPassword, oldPassword));
    }

    @Test
    public void testChangePassword_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.changePassword(
                new PasswordChangeRequest(
                        app.getId(), user.getId(), "Password!1", "Password!"));
    }

    @Test
    public void testChangePassword_WrongPassword() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String oldPassword = "Password";
        String newPassword = "Password!1";
        thrown.expect(BusinessError.class);
        saam.changePassword(
                new PasswordChangeRequest(app.getId(), user.getId(), oldPassword, newPassword));
    }

    @Test
    public void testChangePassword_NoPasswordPolicy() {
        ApplicationResponse app = createSimpleApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));
        String oldPassword = null;
        String newPassword = "Password!1";
        thrown.expect(BusinessError.class);
        saam.changePassword(
                new PasswordChangeRequest(app.getId(), user.getId(), oldPassword, newPassword));
    }

    @Test
    public void testResetPassword() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String identifier = getIdentifier(user, IdentifierType.USERNAME).getContent();
        long timeout = 1000;
        PasswordResetCodeResponse resetCode = saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(),
                        user.getId(),
                        IdentifierType.USERNAME,
                        identifier,
                        timeout));
        String password = "NewPassword!1";
        UserResponse actualUser = saam.resetPassword(
                new PasswordResetRequest(
                        app.getId(),
                        user.getId(),
                        resetCode.getCode(),
                        password));
        saam.changePassword(
                new PasswordChangeRequest(app.getId(), user.getId(), password, "NewPassword2!"));
        assertUser(user, actualUser);
    }

    @Test
    public void testResetPassword_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String identifier = getIdentifier(user, IdentifierType.USERNAME).getContent();
        PasswordResetCodeResponse resetCode = saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(),
                        user.getId(),
                        IdentifierType.USERNAME,
                        identifier,
                        1000));

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.resetPassword(
                new PasswordResetRequest(
                        app.getId(),
                        user.getId(),
                        resetCode.getCode(),
                        "NewPassword!1"));
    }


    @Test
    public void testResetPassword_NoPasswordSet() {
        ApplicationResponse app = createApplication();
        Map<IdentifierType, String> identifiers = new HashMap<>();
        identifiers.put(IdentifierType.USERNAME, getUsername());
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), identifiers, Collections.emptyMap(), null, Collections.emptyList()));
        String identifier = getIdentifier(user, IdentifierType.USERNAME).getContent();
        long timeout = 1000;
        PasswordResetCodeResponse resetCode = saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(),
                        user.getId(),
                        IdentifierType.USERNAME,
                        identifier,
                        timeout));
        String password = "NewPassword!1";
        UserResponse actualUser = saam.resetPassword(
                new PasswordResetRequest(
                        app.getId(),
                        user.getId(),
                        resetCode.getCode(),
                        password));
        saam.changePassword(
                new PasswordChangeRequest(app.getId(), user.getId(), password, "NewPassword2!"));
        assertUser(user, actualUser);
    }

    @Test
    public void testResetPassword_ResetCodeExpired() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String identifier = getIdentifier(user, IdentifierType.USERNAME).getContent();
        long timeout = 100;
        PasswordResetCodeResponse resetCode = saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(),
                        user.getId(),
                        IdentifierType.USERNAME,
                        identifier,
                        timeout));
        String password = "NewPassword!1";
        try {
            Thread.sleep(110);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thrown.expect(BusinessError.class);
        saam.resetPassword(
                new PasswordResetRequest(
                        app.getId(),
                        user.getId(),
                        resetCode.getCode(),
                        password));
    }

    @Test
    public void testResetPassword_InvalidResetCode() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String identifier = getIdentifier(user, IdentifierType.USERNAME).getContent();
        long timeout = 1000;
        PasswordResetCodeResponse resetCode = saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(),
                        user.getId(),
                        IdentifierType.USERNAME,
                        identifier,
                        timeout));
        saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(),
                        user.getId(),
                        IdentifierType.USERNAME,
                        identifier,
                        timeout));
        String password = "NewPassword!1";
        thrown.expect(BusinessError.class);
        saam.resetPassword(
                new PasswordResetRequest(
                        app.getId(),
                        user.getId(),
                        resetCode.getCode(),
                        password));
    }

    @Test
    public void testAddIdentifier() {
        ApplicationResponse app = createApplication();
        String username = getUsername();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));
        UserResponse actualUser = saam.addIdentifier(
                new IdentifierAddRequest(app.getId(), user.getId(), IdentifierType.USERNAME, username));
        assertEquals(user.getApplicationId(), actualUser.getApplicationId());
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getCreationTime(), actualUser.getCreationTime());
        assertEquals(1, actualUser.getIdentifiers().size());
        assertEquals(IdentifierType.USERNAME, actualUser.getIdentifiers().get(0).getType());
        assertEquals(username, actualUser.getIdentifiers().get(0).getContent());
    }

    @Test
    public void testAddIdentifier_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.addIdentifier(
                new IdentifierAddRequest(app.getId(), user.getId(), IdentifierType.USERNAME, getUsername()));
    }

    @Test
    public void testAddIdentifier_DuplicateIdentifier() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String username = getIdentifier(user, IdentifierType.USERNAME).getContent();

        thrown.expect(BusinessError.class);
        saam.addIdentifier(
                new IdentifierAddRequest(app.getId(), user.getId(), IdentifierType.USERNAME, username));
    }

    @Test
    public void testAddIdentifier_WithoutPolicy() {
        ApplicationResponse app = createSimpleApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        String username = getUsername();
        thrown.expect(BusinessError.class);
        saam.addIdentifier(
                new IdentifierAddRequest(app.getId(), user.getId(), IdentifierType.USERNAME, username));
    }

    @Test
    public void testRemoveIdentifier() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String username = getIdentifier(user, IdentifierType.USERNAME).getContent();
        UserResponse actualUser = saam.removeIdentifier(
                new IdentifierRemoveRequest(app.getId(), user.getId(), IdentifierType.USERNAME, username));
        assertEquals(user.getApplicationId(), actualUser.getApplicationId());
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getCreationTime(), actualUser.getCreationTime());
        assertNull(getIdentifier(actualUser, IdentifierType.USERNAME));
    }

    @Test
    public void testRemoveIdentifier_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String username = getIdentifier(user, IdentifierType.USERNAME).getContent();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeIdentifier(
                new IdentifierRemoveRequest(app.getId(), user.getId(), IdentifierType.USERNAME, username));
    }

    @Test
    public void testRemoveIdentifier_NotExist() {
        ApplicationResponse app = createApplication();
        String username = getUsername();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        thrown.expect(BusinessError.class);
        saam.removeIdentifier(
                new IdentifierRemoveRequest(app.getId(), user.getId(), IdentifierType.USERNAME, username));
    }

    @Test
    public void testVerifyIdentifier() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.Identifier email = getIdentifier(user, IdentifierType.EMAIL);
        IdentifierVerificationCodeResponse code = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), email.getContent(), 1000));
        UserResponse actualUser = saam.verifyIdentifier(
                new IdentifierVerifyRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), code.getCode()));
        email = getIdentifier(actualUser, IdentifierType.EMAIL);
        assertEquals(user.getApplicationId(), actualUser.getApplicationId());
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getCreationTime(), actualUser.getCreationTime());
        assertEquals(true, email.isVerified());
    }

    @Test
    public void testVerifyIdentifier_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.Identifier email = getIdentifier(user, IdentifierType.EMAIL);
        IdentifierVerificationCodeResponse code = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), email.getContent(), 1000));
        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.verifyIdentifier(
                new IdentifierVerifyRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), code.getCode()));
    }

    @Test
    public void testVerifyIdentifier_AlreadyVerified() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.Identifier email = getIdentifier(user, IdentifierType.EMAIL);
        IdentifierVerificationCodeResponse code = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), email.getContent(), 1000));
        saam.verifyIdentifier(
                new IdentifierVerifyRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), code.getCode()));

        code = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), email.getContent(), 1000));
        UserResponse actualUser = saam.verifyIdentifier(
                new IdentifierVerifyRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), code.getCode()));
        email = getIdentifier(actualUser, IdentifierType.EMAIL);
        assertEquals(user.getApplicationId(), actualUser.getApplicationId());
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getCreationTime(), actualUser.getCreationTime());
        assertEquals(true, email.isVerified());
    }

    @Test
    public void testVerifyIdentifier_InvalidVerificationCode() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.Identifier email = getIdentifier(user, IdentifierType.EMAIL);
        IdentifierVerificationCodeResponse code = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), email.getContent(), 1000));
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), email.getContent(), 1000));

        thrown.expect(BusinessError.class);
        saam.verifyIdentifier(
                new IdentifierVerifyRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), code.getCode()));
    }

    @Test
    public void testConnectOAuthIdentifier() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));
        String googleOAuthIdentifier = getGoogleOAuthIdentifier();
        UserResponse actualUser = saam.connectOAuthIdentifier(
                new OAuthIdentifierConnectRequest(
                        app.getId(),
                        user.getId(),
                        OAuthPlatform.GOOGLE,
                        googleOAuthIdentifier,
                        Collections.emptyMap()));
        UserResponse.OAuthIdentifier identifier = getOAuthIdentifier(actualUser, OAuthPlatform.GOOGLE);
        assertEquals(user.getApplicationId(), actualUser.getApplicationId());
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getCreationTime(), actualUser.getCreationTime());
        assertNotNull(identifier);
        assertEquals(OAuthPlatform.GOOGLE, identifier.getPlatform());
        assertEquals(googleOAuthIdentifier, identifier.getContent());
        assertEquals(0, identifier.getProperties().size());
    }

    @Test
    public void testConnectOAuthIdentifier_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));
        String googleOAuthIdentifier = getGoogleOAuthIdentifier();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.connectOAuthIdentifier(
                new OAuthIdentifierConnectRequest(
                        app.getId(),
                        user.getId(),
                        OAuthPlatform.GOOGLE,
                        googleOAuthIdentifier,
                        Collections.emptyMap()));
    }

    @Test
    public void testConnectOAuthIdentifier_Duplicate() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));
        String googleOAuthIdentifier = getGoogleOAuthIdentifier();

        saam.connectOAuthIdentifier(
                new OAuthIdentifierConnectRequest(
                        app.getId(),
                        user.getId(),
                        OAuthPlatform.GOOGLE,
                        googleOAuthIdentifier,
                        Collections.emptyMap()));

        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        UserResponse actualUser = saam.connectOAuthIdentifier(
                new OAuthIdentifierConnectRequest(
                        app.getId(),
                        user.getId(),
                        OAuthPlatform.GOOGLE,
                        googleOAuthIdentifier,
                        properties));
        UserResponse.OAuthIdentifier identifier = getOAuthIdentifier(actualUser, OAuthPlatform.GOOGLE);
        assertEquals(user.getApplicationId(), actualUser.getApplicationId());
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getCreationTime(), actualUser.getCreationTime());
        assertNotNull(identifier);
        assertEquals(OAuthPlatform.GOOGLE, identifier.getPlatform());
        assertEquals(googleOAuthIdentifier, identifier.getContent());
        assertEquals(properties, identifier.getProperties());
    }

    @Test
    public void testConnectOAuthIdentifier_WithoutPolicy() {
        ApplicationResponse app = createSimpleApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        String googleOAuthIdentifier = getGoogleOAuthIdentifier();

        thrown.expect(BusinessError.class);
        saam.connectOAuthIdentifier(
                new OAuthIdentifierConnectRequest(
                        app.getId(),
                        user.getId(),
                        OAuthPlatform.GOOGLE,
                        googleOAuthIdentifier,
                        Collections.emptyMap()));
    }

    @Test
    public void testDisconnectOAuthIdentifier() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String googleOAuthIdentifier = getOAuthIdentifier(user, OAuthPlatform.GOOGLE).getContent();
        UserResponse actualUser = saam.disconnectOAuthIdentifier(
                new OAuthIdentifierDisconnectRequest(
                        app.getId(), user.getId(), OAuthPlatform.GOOGLE, googleOAuthIdentifier));
        UserResponse.OAuthIdentifier identifier = getOAuthIdentifier(actualUser, OAuthPlatform.GOOGLE);
        assertEquals(user.getApplicationId(), actualUser.getApplicationId());
        assertEquals(user.getId(), actualUser.getId());
        assertEquals(user.getCreationTime(), actualUser.getCreationTime());
        assertNull(identifier);
    }

    @Test
    public void testDisconnectOAuthIdentifier_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String googleOAuthIdentifier = getOAuthIdentifier(user, OAuthPlatform.GOOGLE).getContent();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.disconnectOAuthIdentifier(
                new OAuthIdentifierDisconnectRequest(
                        app.getId(), user.getId(), OAuthPlatform.GOOGLE, googleOAuthIdentifier));
    }

    @Test
    public void testDisconnectOAuthIdentifier_NotExist() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        String googleOAuthIdentifier = getGoogleOAuthIdentifier();

        thrown.expect(BusinessError.class);
        saam.disconnectOAuthIdentifier(
                new OAuthIdentifierDisconnectRequest(
                        app.getId(), user.getId(), OAuthPlatform.GOOGLE, googleOAuthIdentifier));
    }

    @Test
    public void testAddAPIKey() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        String name = getAPIKeyName();
        APIKeyResponse apiKey = saam.addAPIKey(
                new APIKeyAddRequest(app.getId(), user.getId(), name));
        assertNotNull(apiKey);
        assertEquals(app.getId(), apiKey.getApplicationId());
        assertEquals(user.getId(), apiKey.getUserId());
        assertEquals(name, apiKey.getName());
    }

    @Test
    public void testAddAPIKey_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));
        String name = getAPIKeyName();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.addAPIKey(
                new APIKeyAddRequest(app.getId(), user.getId(), name));
    }

    @Test
    public void testAddAPIKey_NoUserFound() {
        ApplicationResponse app = createApplication();
        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));
        saam.removeUser(app.getId(), user.getId());

        thrown.expect(BusinessError.class);
        String name = getAPIKeyName();
        saam.addAPIKey(
                new APIKeyAddRequest(app.getId(), user.getId(), name));
    }

    @Test
    public void testUpdateAPIKey() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String name = getAPIKeyName();
        boolean isActive = false;
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        APIKeyResponse actualAPIKey = saam.updateAPIKey(
                new APIKeyUpdateRequest(apiKey.getApplicationId(), apiKey.getId(), name, isActive));
        assertEquals(apiKey.getApplicationId(), actualAPIKey.getApplicationId());
        assertEquals(apiKey.getId(), apiKey.getId());
        assertEquals(apiKey.getSecretKey(), apiKey.getSecretKey());
        assertEquals(apiKey.getUserId(), actualAPIKey.getUserId());
        assertEquals(name, actualAPIKey.getName());
        assertEquals(isActive, actualAPIKey.isActive());
    }

    @Test
    public void testUpdateAPIKey_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String name = getAPIKeyName();
        boolean isActive = false;
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.updateAPIKey(
                new APIKeyUpdateRequest(apiKey.getApplicationId(), apiKey.getId(), name, isActive));
    }

    @Test
    public void testUpdateAPIKey_NotExist() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String name = getAPIKeyName();
        boolean isActive = false;
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        saam.removeAPIKeyById(apiKey.getApplicationId(), apiKey.getId());

        thrown.expect(BusinessError.class);
        saam.updateAPIKey(
                new APIKeyUpdateRequest(apiKey.getApplicationId(), apiKey.getId(), name, isActive));
    }

    @Test
    public void testVerifyAPIkey() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        saam.verifyAPIKey(
                new APIKeyVerifyRequest(
                        apiKey.getApplicationId(), apiKey.getId(), apiKey.getSecretKey()));
    }

    @Test
    public void testVerifyAPIkey_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.verifyAPIKey(
                new APIKeyVerifyRequest(
                        apiKey.getApplicationId(), apiKey.getId(), apiKey.getSecretKey()));
    }

    @Test
    public void testVerifyAPIkey_NotExist() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        saam.removeAPIKeyById(apiKey.getApplicationId(), apiKey.getId());

        thrown.expect(BusinessError.class);
        saam.verifyAPIKey(
                new APIKeyVerifyRequest(
                        apiKey.getApplicationId(), apiKey.getId(), apiKey.getSecretKey()));
    }

    @Test
    public void testVerifyAPIkey_WrongSecretKey() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());

        thrown.expect(BusinessError.class);
        saam.verifyAPIKey(
                new APIKeyVerifyRequest(
                        apiKey.getApplicationId(), apiKey.getId(), "wrongkey"));
    }

    @Test
    public void testVerifyAPIkey_Inactive() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        saam.updateAPIKey(
                new APIKeyUpdateRequest(
                        apiKey.getApplicationId(), apiKey.getId(), apiKey.getName(), false));

        thrown.expect(BusinessError.class);
        saam.verifyAPIKey(
                new APIKeyVerifyRequest(
                        apiKey.getApplicationId(), apiKey.getId(), "wrongkey"));
    }

    @Test
    public void testVerifyAPIkey_NoSuchUser() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        saam.removeUser(apiKey.getApplicationId(), apiKey.getUserId());

        thrown.expect(BusinessError.class);
        saam.verifyAPIKey(
                new APIKeyVerifyRequest(
                        apiKey.getApplicationId(), apiKey.getId(), "wrongkey"));
    }

    @Test
    public void testRemoveAPIKeyById() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        saam.removeAPIKeyById(apiKey.getApplicationId(), apiKey.getId());
        APIKeyResponse actualAPIKey = saam.getAPIKeyById(apiKey.getApplicationId(), apiKey.getId());
        assertNull(actualAPIKey);
    }

    @Test
    public void testRemoveAPIKeyById_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeAPIKeyById(apiKey.getApplicationId(), apiKey.getId());
    }

    @Test
    public void testRemoveAPIKey_NotExist() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        saam.removeAPIKeyById(apiKey.getApplicationId(), apiKey.getId());

        thrown.expect(BusinessError.class);
        saam.removeAPIKeyById(apiKey.getApplicationId(), apiKey.getId());
    }

    @Test
    public void testListAPIKeysByApplicationIdAndUserId() {
        ApplicationResponse app = createApplication();
        UserResponse user1 = registerUser(app.getId());
        List<APIKeyResponse> apiKeys1 = createAPIKeys(app.getId(), user1.getId(), 3);

        UserResponse user2 = registerUser(app.getId());
        createAPIKeys(app.getId(), user2.getId(), 5);

        List<APIKeyResponse> actualAPIKeys1 = saam.listAPIKeysByApplicationIdAndUserId(app.getId(), user1.getId());
        assertEquals(apiKeys1.size(), actualAPIKeys1.size());

        for (APIKeyResponse apiKey: apiKeys1) {
            for (APIKeyResponse actualAPIKey: actualAPIKeys1) {
                if (apiKey.getSecretKey().equals(actualAPIKey.getSecretKey())) {
                    assertAPIKey(apiKey, actualAPIKey);
                }
            }
        }
    }

    @Test
    public void testLogin_Credential() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.Identifier username = getIdentifier(user, IdentifierType.USERNAME);
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");
        UserSessionResponse session = saam.login(
                new CredentialLoginRequest(
                        app.getId(),
                        username.getContent(),
                        "Password!",
                        sessionDetails,
                        1000));
        assertNotNull(session);
        assertEquals(app.getId(), session.getApplicationId());
        assertEquals(user.getId(), session.getUserId());
        assertEquals(sessionDetails, session.getDetails());
        assertNotNull(session.getKey());
        assertNotNull(session.getCreationTime());
        assertTrue((session.getExpirationTime().getTime() > session.getCreationTime().getTime()));
    }

    @Test
    public void testLogin_Credential_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.Identifier username = getIdentifier(user, IdentifierType.USERNAME);
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.login(
                new CredentialLoginRequest(
                        app.getId(),
                        username.getContent(),
                        "Password!",
                        sessionDetails,
                        1000));
    }

    @Test
    public void testLogin_Credential_NoPolicyFound() {
        ApplicationResponse app = createSimpleApplication();
        saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        thrown.expect(BusinessError.class);
        saam.login(
                new CredentialLoginRequest(
                        app.getId(),
                        "foo",
                        "Password!",
                        Collections.emptyMap(),
                        1000));
    }

    @Test
    public void testLogin_Credential_NoUserFound() {
        ApplicationResponse app = createApplication();

        thrown.expect(BusinessError.class);
        saam.login(
                new CredentialLoginRequest(
                        app.getId(),
                        "foo",
                        "Password!",
                        Collections.emptyMap(),
                        1000));
    }

    @Test
    public void testLogin_Credential_WrongPassword() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.Identifier username = getIdentifier(user, IdentifierType.USERNAME);

        thrown.expect(BusinessError.class);
        saam.login(
                new CredentialLoginRequest(
                        app.getId(),
                        username.getContent(),
                        "WrongPassword",
                        Collections.emptyMap(),
                        1000));
    }

    @Test
    public void testLogin_OAuth() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.OAuthIdentifier googleOAuth = getOAuthIdentifier(user, OAuthPlatform.GOOGLE);
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");
        UserSessionResponse session = saam.login(
                new OAuthLoginRequest(
                        app.getId(),
                        googleOAuth.getPlatform(),
                        googleOAuth.getContent(),
                        sessionDetails,
                        1000));
        assertNotNull(session);
        assertEquals(app.getId(), session.getApplicationId());
        assertEquals(user.getId(), session.getUserId());
        assertEquals(sessionDetails, session.getDetails());
        assertNotNull(session.getKey());
        assertNotNull(session.getCreationTime());
        assertTrue((session.getExpirationTime().getTime() > session.getCreationTime().getTime()));
    }

    @Test
    public void testLogin_OAuth_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.OAuthIdentifier googleOAuth = getOAuthIdentifier(user, OAuthPlatform.GOOGLE);
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.login(
                new OAuthLoginRequest(
                        app.getId(),
                        googleOAuth.getPlatform(),
                        googleOAuth.getContent(),
                        sessionDetails,
                        1000));
    }

    @Test
    public void testLogin_OAuth_NoSuchOAuthPolicy() {
        ApplicationResponse app = createApplication();
        registerUser(app.getId());
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");

        thrown.expect(BusinessError.class);
        saam.login(
                new OAuthLoginRequest(
                        app.getId(),
                        OAuthPlatform.SLACK,
                        "slack-id",
                        sessionDetails,
                        1000));
    }

    @Test
    public void testLogin_OAuth_NoSuchOAuthIdentifier() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.OAuthIdentifier googleOAuth = getOAuthIdentifier(user, OAuthPlatform.GOOGLE);
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");

        thrown.expect(BusinessError.class);
        saam.login(
                new OAuthLoginRequest(
                        app.getId(),
                        googleOAuth.getPlatform(),
                        getGoogleOAuthIdentifier(),
                        sessionDetails,
                        1000));
    }

    @Test
    public void testLogin_OAuth_NoSuchUser() {
        ApplicationResponse app = createApplication();
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");

        thrown.expect(BusinessError.class);
        saam.login(
                new OAuthLoginRequest(
                        app.getId(),
                        OAuthPlatform.GOOGLE,
                        getGoogleOAuthIdentifier(),
                        sessionDetails,
                        1000));
    }

    @Test
    public void testCreateUserSession() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");
        UserSessionResponse session = saam.createUserSession(
                new UserSessionCreateRequest(
                        app.getId(),
                        user.getId(),
                        sessionDetails,
                        1000));
        assertNotNull(session);
        assertEquals(app.getId(), session.getApplicationId());
        assertEquals(user.getId(), session.getUserId());
        assertEquals(sessionDetails, session.getDetails());
        assertNotNull(session.getKey());
        assertNotNull(session.getCreationTime());
        assertTrue((session.getExpirationTime().getTime() > session.getCreationTime().getTime()));
    }

    @Test
    public void testCreateUserSession_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.createUserSession(
                new UserSessionCreateRequest(
                        app.getId(),
                        user.getId(),
                        sessionDetails,
                        1000));
    }

    @Test
    public void testCreateUserSession_NoSuchUser() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        saam.removeUser(app.getId(), user.getId());
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");

        thrown.expect(BusinessError.class);
        saam.createUserSession(
                new UserSessionCreateRequest(
                        app.getId(),
                        user.getId(),
                        sessionDetails,
                        1000));
    }

    @Test
    public void testGetUserSessionByKey() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");
        UserSessionResponse session = saam.createUserSession(
                new UserSessionCreateRequest(
                        app.getId(),
                        user.getId(),
                        sessionDetails,
                        1000));
        UserSessionResponse actualSession = saam.getUserSessionByKey(app.getId(), session.getKey());

        assertEquals(session.getApplicationId(), actualSession.getApplicationId());
        assertEquals(session.getUserId(), actualSession.getUserId());
        assertEquals(session.getDetails(), actualSession.getDetails());
        assertEquals(session.getKey(), actualSession.getKey());
        assertEquals(session.getCreationTime(), actualSession.getCreationTime());
        assertEquals(session.getExpirationTime(), actualSession.getExpirationTime());
    }

    @Test
    public void testRemoveUserSessionByKey() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");
        UserSessionResponse session = saam.createUserSession(
                new UserSessionCreateRequest(
                        app.getId(),
                        user.getId(),
                        sessionDetails,
                        1000));

        saam.removeUserSessionByKey(session.getApplicationId(), session.getKey());
        UserSessionResponse actualSession = saam.getUserSessionByKey(app.getId(), session.getKey());
        assertNull(actualSession);
    }

    @Test
    public void testRemoveUserSessionByKey_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");
        UserSessionResponse session = saam.createUserSession(
                new UserSessionCreateRequest(
                        app.getId(),
                        user.getId(),
                        sessionDetails,
                        1000));

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeUserSessionByKey(session.getApplicationId(), session.getKey());
    }

    @Test
    public void testRemoveUserSessionByKey_NotExist() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");
        UserSessionResponse session = saam.createUserSession(
                new UserSessionCreateRequest(
                        app.getId(),
                        user.getId(),
                        sessionDetails,
                        1000));

        saam.removeUserSessionByKey(session.getApplicationId(), session.getKey());

        thrown.expect(BusinessError.class);
        saam.removeUserSessionByKey(session.getApplicationId(), session.getKey());
    }

    @Test
    public void testRemoveUserSessionsByUserId() {
        ApplicationResponse app = createApplication();
        UserResponse user1 = registerUser(app.getId());
        createUserSessions(app.getId(), user1.getId(), 3);

        UserResponse user2 = registerUser(app.getId());
        createUserSessions(app.getId(), user2.getId(), 5);
        saam.removeUserSessionsByUserId(app.getId(), user1.getId());

        PaginatedResult<List<UserSessionResponse>> actualResult1 =
                saam.listUserSessionsByUserId(app.getId(), user1.getId());
        List<UserSessionResponse> actualSessions1 =
                actualResult1.start(new OffsetBasedResultPage(1, 10)).getResult();

        PaginatedResult<List<UserSessionResponse>> actualResult2 =
                saam.listUserSessionsByUserId(app.getId(), user2.getId());
        List<UserSessionResponse> actualSessions2 =
                actualResult2.start(new OffsetBasedResultPage(1, 10)).getResult();

        assertEquals(0, actualSessions1.size());
        assertEquals(5, actualSessions2.size());
    }

    @Test
    public void testAddRole() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        RoleResponse role = saam.addRole(new RoleAddRequest(app.getId(), name));
        assertEquals(app.getId(), role.getApplicationId());
        assertEquals(name, role.getName());
    }

    @Test
    public void testAddRole_InactiveApplication() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.addRole(new RoleAddRequest(app.getId(), name));
    }

    @Test
    public void testAddRole_DuplicateName() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        saam.addRole(new RoleAddRequest(app.getId(), name));

        thrown.expect(BusinessError.class);
        saam.addRole(new RoleAddRequest(app.getId(), name));
    }

    @Test
    public void testUpdateRole() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        RoleResponse role = saam.addRole(new RoleAddRequest(app.getId(), name));
        String newName = getRoleName();
        RoleResponse actualRole = saam.updateRole(new RoleUpdateRequest(app.getId(), role.getId(), newName));

        assertEquals(role.getApplicationId(), role.getApplicationId());
        assertEquals(role.getId(), role.getId());
        assertEquals(newName, actualRole.getName());
    }

    @Test
    public void testUpdateRole_InactiveApplication() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        RoleResponse role = saam.addRole(new RoleAddRequest(app.getId(), name));
        String newName = getRoleName();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.updateRole(new RoleUpdateRequest(app.getId(), role.getId(), newName));
    }

    @Test
    public void testUpdateRole_SameName() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        RoleResponse role = saam.addRole(new RoleAddRequest(app.getId(), name));
        RoleResponse actualRole = saam.updateRole(new RoleUpdateRequest(app.getId(), role.getId(), name));

        assertEquals(role.getApplicationId(), role.getApplicationId());
        assertEquals(role.getId(), role.getId());
        assertEquals(name, actualRole.getName());
    }

    @Test
    public void testUpdateRole_NotExist() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        RoleResponse role = saam.addRole(new RoleAddRequest(app.getId(), name));
        saam.removeRole(app.getId(), role.getId());

        thrown.expect(BusinessError.class);
        saam.updateRole(new RoleUpdateRequest(app.getId(), role.getId(), getRoleName()));
    }

    @Test
    public void testRemoveRole() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        RoleResponse role = saam.addRole(new RoleAddRequest(app.getId(), name));
        saam.removeRole(app.getId(), role.getId());
        RoleResponse actualRole = saam.getRoleById(app.getId(), role.getId());
        assertNull(actualRole);
    }

    @Test
    public void testRemoveRole_InactiveApplication() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        RoleResponse role = saam.addRole(new RoleAddRequest(app.getId(), name));

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeRole(app.getId(), role.getId());
    }

    @Test
    public void testRemoveRole_NotExist() {
        ApplicationResponse app = createApplication();
        String name = getRoleName();
        RoleResponse role = saam.addRole(new RoleAddRequest(app.getId(), name));
        saam.removeRole(app.getId(), role.getId());

        thrown.expect(BusinessError.class);
        saam.removeRole(app.getId(), role.getId());
    }

    @Test
    public void testListRoles() {
        ApplicationResponse app1 = createApplication();
        List<RoleResponse> roles1 = createRoles(app1.getId(), 3);
        ApplicationResponse app2 = createApplication();
        List<RoleResponse> roles2 = createRoles(app2.getId(), 5);


        PaginatedResult<List<RoleResponse>> actualResult1 = saam.listRoles(app1.getId());
        List<RoleResponse> actualRoles1 = actualResult1.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(roles1.size(), actualRoles1.size());
        for (RoleResponse role: roles1) {
            for (RoleResponse actualRole: actualRoles1) {
                if (role.getId().equals(actualRole.getId())) {
                    assertRole(role, actualRole);
                }
            }
        }

        PaginatedResult<List<RoleResponse>> actualResult2 = saam.listRoles(app2.getId());
        List<RoleResponse> actualRoles2 = actualResult2.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(roles2.size(), actualRoles2.size());
    }

    @Test
    public void testStoreResource_UserOwner() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String key = getResourceKey();
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
        assertNotNull(resource);
        assertEquals(app.getId(), resource.getApplicationId());
        assertEquals(user.getId(), resource.getOwnerId());
        assertEquals(key, resource.getKey());
    }

    @Test
    public void testStoreResource_UserOwner_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String key = getResourceKey();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
    }

    @Test
    public void testStoreResource_RoleOwner() {
        ApplicationResponse app = createApplication();
        RoleResponse role = createRole(app.getId(), getRoleName());
        String key = getResourceKey();
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), role.getId(), key));
        assertNotNull(resource);
        assertEquals(app.getId(), resource.getApplicationId());
        assertEquals(role.getId(), resource.getOwnerId());
        assertEquals(key, resource.getKey());
    }

    @Test
    public void testStoreResource_RoleOwner_InactiveApplication() {
        ApplicationResponse app = createApplication();
        RoleResponse role = createRole(app.getId(), getRoleName());
        String key = getResourceKey();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.storeResource(
                new ResourceStoreRequest(app.getId(), role.getId(), key));
    }

    @Test
    public void testStoreResource_APIKeyOwner() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        String key = getResourceKey();
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), apiKey.getId(), key));
        assertNotNull(resource);
        assertEquals(app.getId(), resource.getApplicationId());
        assertEquals(user.getId(), resource.getOwnerId());
        assertEquals(key, resource.getKey());
    }

    @Test
    public void testStoreResource_APIKeyOwner_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        APIKeyResponse apiKey = createAPIKey(app.getId(), user.getId());
        String key = getResourceKey();

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.storeResource(
                new ResourceStoreRequest(app.getId(), apiKey.getId(), key));
    }

    @Test
    public void testStoreResource_NotExitOwner() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        saam.removeUser(user.getApplicationId(), user.getId());
        String key = getResourceKey();

        thrown.expect(BusinessError.class);
        saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
    }

    @Test
    public void testStoreResource_DuplicateKey() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String key = getResourceKey();
        saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
        assertNotNull(resource);
        assertEquals(app.getId(), resource.getApplicationId());
        assertEquals(user.getId(), resource.getOwnerId());
        assertEquals(key, resource.getKey());
    }

    @Test
    public void testStoreResource_InvalidKey_IllegalFormat() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String key = "key!";

        thrown.expect(BusinessError.class);
        saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
    }

    @Test
    public void testStoreResource_InvalidKey_TooShort() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String key = "";

        thrown.expect(BusinessError.class);
        saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
    }

    @Test
    public void testStoreResource_InvalidKey_TooLong() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 126; i ++) {
            key.append("a");
        }
        thrown.expect(BusinessError.class);
        saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key.toString()));
    }

    @Test
    public void testStoreResource_NoParentExist() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String key = getResourceKey(getResourceKey());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
        assertNotNull(resource);
        assertEquals(app.getId(), resource.getApplicationId());
        assertEquals(user.getId(), resource.getOwnerId());
        assertEquals(key, resource.getKey());
    }

    @Test
    public void testRemoveResource() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String key = getResourceKey();
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));
        saam.removeResource(resource.getApplicationId(), resource.getKey());
        ResourceResponse actualResource =
                saam.getResourceByKey(resource.getApplicationId(), resource.getKey());
        assertNull(actualResource);
    }

    @Test
    public void testRemoveResource_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String key = getResourceKey();
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), key));

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removeResource(resource.getApplicationId(), resource.getKey());
    }

    @Test
    public void testRemoveResource_Parent() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        String parentKey = getResourceKey();
        ResourceResponse parent = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), parentKey));
        createResources(app.getId(), user.getId(), parentKey, 3);
        saam.removeResource(app.getId(), parentKey);

        ResourceResponse actualResource =
                saam.getResourceByKey(app.getId(), parent.getKey());
        assertNull(actualResource);
    }

    @Test
    public void testRemoveResource_NotExist() {
        ApplicationResponse app = createApplication();
        String key = getResourceKey();

        thrown.expect(BusinessError.class);
        saam.removeResource(app.getId(), key);
    }

    @Test
    public void testGetResourcesByOwnerId() {
        ApplicationResponse app = createApplication();
        UserResponse user1 = registerUser(app.getId());
        List<ResourceResponse> resources1 =
                createResources(app.getId(), user1.getId(), null,3);

        UserResponse user2 = registerUser(app.getId());
        List<ResourceResponse> resources2 =
                createResources(app.getId(), user2.getId(), null,5);

        PaginatedResult<List<ResourceResponse>> actualResult1 =
                saam.getResourcesByOwnerId(app.getId(), user1.getId(), null);
        List<ResourceResponse> actualResources1 =
                actualResult1.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(resources1.size(), actualResources1.size());
        for (ResourceResponse resource: resources1) {
            for (ResourceResponse actualResource: actualResources1) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }

        PaginatedResult<List<ResourceResponse>> actualResult2 =
                saam.getResourcesByOwnerId(app.getId(), user2.getId(), null);
        List<ResourceResponse> actualResources2 =
                actualResult2.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(resources2.size(), actualResources2.size());
        for (ResourceResponse resource: resources2) {
            for (ResourceResponse actualResource: actualResources2) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }
    }

    @Test
    public void testGetResourcesByOwnerId_Parent() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        ResourceResponse parentParent = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), getResourceKey()));
        List<ResourceResponse> parents =
                createResources(app.getId(), user.getId(), parentParent.getKey(),5);
        ResourceResponse parent = parents.get(0);
        List<ResourceResponse> resources =
                createResources(app.getId(), user.getId(), parent.getKey(),3);

        PaginatedResult<List<ResourceResponse>> actualResult =
                saam.getResourcesByOwnerId(app.getId(), user.getId(), parent.getKey());
        List<ResourceResponse> actualResources =
                actualResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(resources.size(), actualResources.size());
        for (ResourceResponse resource: resources) {
            for (ResourceResponse actualResource: actualResources) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }

        PaginatedResult<List<ResourceResponse>> actualParentResult =
                saam.getResourcesByOwnerId(app.getId(), user.getId(), parent.getParentKey());
        List<ResourceResponse> actualParents =
                actualParentResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(parents.size(), actualParents.size());
        for (ResourceResponse resource: parents) {
            for (ResourceResponse actualResource: actualParents) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }
    }

    @Test
    public void testGetGrantedResources() {
        ApplicationResponse app = createApplication();
        UserResponse user1 = registerUser(app.getId());
        List<ResourceResponse> resources1 =
                createResources(app.getId(), user1.getId(), null,3);
        for (ResourceResponse resource: resources1) {
            createPermission(app.getId(), resource.getKey(),  user1.getId());
        }

        UserResponse user2 = registerUser(app.getId());
        createResources(app.getId(), user2.getId(), null,5);

        PaginatedResult<List<ResourceResponse>> actualResult1 =
                saam.getGrantedResources(app.getId(), user1.getId());
        List<ResourceResponse> actualResources1 =
                actualResult1.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(resources1.size(), actualResources1.size());
        for (ResourceResponse resource: resources1) {
            for (ResourceResponse actualResource: actualResources1) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }

        PaginatedResult<List<ResourceResponse>> actualResult2 =
                saam.getGrantedResources(app.getId(), user2.getId());
        List<ResourceResponse> actualResources2 =
                actualResult2.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(0, actualResources2.size());
    }

    @Test
    public void testListResources() {
        ApplicationResponse app1 = createApplication();
        UserResponse user1 = registerUser(app1.getId());
        List<ResourceResponse> resources1 =
                createResources(app1.getId(), user1.getId(), null,3);

        ApplicationResponse app2 = createApplication();
        UserResponse user2 = registerUser(app2.getId());
        List<ResourceResponse> resources2 =
                createResources(app2.getId(), user2.getId(), null,5);

        PaginatedResult<List<ResourceResponse>> actualResult1 =
                saam.listResources(app1.getId(), null);
        List<ResourceResponse> actualResources1 =
                actualResult1.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(resources1.size(), actualResources1.size());
        for (ResourceResponse resource: resources1) {
            for (ResourceResponse actualResource: actualResources1) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }

        PaginatedResult<List<ResourceResponse>> actualResult2 =
                saam.listResources(app2.getId(), null);
        List<ResourceResponse> actualResources2 =
                actualResult2.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(resources2.size(), actualResources2.size());
        for (ResourceResponse resource: resources2) {
            for (ResourceResponse actualResource: actualResources2) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }
    }

    @Test
    public void testListResources_Parent() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        ResourceResponse parentParent = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), getResourceKey()));
        List<ResourceResponse> parents =
                createResources(app.getId(), user.getId(), parentParent.getKey(),5);
        ResourceResponse parent = parents.get(0);
        List<ResourceResponse> resources =
                createResources(app.getId(), user.getId(), parent.getKey(),3);

        PaginatedResult<List<ResourceResponse>> actualResult =
                saam.listResources(app.getId(), parent.getKey());
        List<ResourceResponse> actualResources =
                actualResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(resources.size(), actualResources.size());
        for (ResourceResponse resource: resources) {
            for (ResourceResponse actualResource: actualResources) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }

        PaginatedResult<List<ResourceResponse>> actualParentResult =
                saam.listResources(app.getId(), parent.getParentKey());
        List<ResourceResponse> actualParents =
                actualParentResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(parents.size(), actualParents.size());
        for (ResourceResponse resource: parents) {
            for (ResourceResponse actualResource: actualParents) {
                if (resource.getKey().equals(actualResource.getKey())) {
                    assertResource(resource, actualResource);
                }
            }
        }
    }

    @Test
    public void testStorePermission() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), getResourceKey()));
        PermissionResponse permission = createPermission(app.getId(), resource.getKey(), user.getId());

        assertNotNull(permission);
        assertEquals(app.getId(), permission.getApplicationId());
        assertEquals(resource.getKey(), permission.getResourceKey());
        assertEquals(user.getId(), permission.getPrincipalId());
    }

    @Test
    public void testStorePermission_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), getResourceKey()));

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        createPermission(app.getId(), resource.getKey(), user.getId());
    }

    @Test
    public void testStorePermission_Update() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), getResourceKey()));
        createPermission(app.getId(), resource.getKey(), user.getId());


        List<Action> actions2 = new LinkedList<>();
        actions2.add(new Action("READ", false));
        actions2.add(new Action("EDIT", false));
        actions2.add(new Action("REMOVE", true));
        actions2.add(new Action("MOVE", true));
        PermissionResponse permission =
                saam.storePermission(
                        new PermissionStoreRequest(
                                app.getId(),
                                resource.getKey(),
                                user.getId(),
                                actions2));

        assertNotNull(permission);
        assertEquals(app.getId(), permission.getApplicationId());
        assertEquals(resource.getKey(), permission.getResourceKey());
        assertEquals(user.getId(), permission.getPrincipalId());
        assertEquals(actions2, permission.getActions());
    }

    @Test
    public void testRemovePermission() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), getResourceKey()));
        createPermission(app.getId(), resource.getKey(), user.getId());

        saam.removePermission(app.getId(), resource.getKey(), user.getId());
        PermissionResponse actualPermission =
                saam.getPermissionByPrincipalId(app.getId(), resource.getKey(), user.getId());
        assertNull(actualPermission);
    }

    @Test
    public void testRemovePermission_InactiveApplication() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), getResourceKey()));
        createPermission(app.getId(), resource.getKey(), user.getId());

        updateApplicationStatus(app, ApplicationStatus.DEACTIVE);

        thrown.expect(BusinessError.class);
        saam.removePermission(app.getId(), resource.getKey(), user.getId());
    }

    @Test
    public void testRemovePermission_NotExist() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), user.getId(), getResourceKey()));
        createPermission(app.getId(), resource.getKey(), user.getId());

        saam.removePermission(app.getId(), resource.getKey(), user.getId());

        thrown.expect(BusinessError.class);
        saam.removePermission(app.getId(), resource.getKey(), user.getId());
    }

    @Test
    public void testListPermissions() {
        ApplicationResponse app = createApplication();
        RoleResponse owner = createRole(app.getId(), getRoleName());
        List<UserResponse> users = registerUsers(app.getId(), 3);
        ResourceResponse resource1 = saam.storeResource(
                new ResourceStoreRequest(app.getId(), owner.getId(), getResourceKey()));
        List<PermissionResponse> permissions1 = createPermissions(app.getId(), resource1.getKey(), users);
        ResourceResponse resource2 = saam.storeResource(
                new ResourceStoreRequest(app.getId(), owner.getId(), getResourceKey()));
        List<PermissionResponse> permissions2 = createPermissions(app.getId(), resource1.getKey(), users);

        PaginatedResult<List<PermissionResponse>> actualResult1 = saam.listPermissions(app.getId(), resource1.getKey());
        List<PermissionResponse> actualPermissions1 =
                actualResult1.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(permissions1.size(), actualPermissions1.size());
        for (PermissionResponse permission: permissions1) {
            for (PermissionResponse actualPermission: actualPermissions1) {
                if (permission.getPrincipalId().equals(actualPermission.getPrincipalId())) {
                    assertPermission(permission, actualPermission);
                }
            }
        }

        PaginatedResult<List<PermissionResponse>> actualResult2 = saam.listPermissions(app.getId(), resource2.getKey());
        List<PermissionResponse> actualPermissions2 =
                actualResult2.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(permissions1.size(), actualPermissions1.size());
        for (PermissionResponse permission: permissions2) {
            for (PermissionResponse actualPermission: actualPermissions2) {
                if (permission.getPrincipalId().equals(actualPermission.getPrincipalId())) {
                    assertPermission(permission, actualPermission);
                }
            }
        }
    }

    @Test
    public void testListPermissions_Parent() {
        ApplicationResponse app = createApplication();
        RoleResponse owner = createRole(app.getId(), getRoleName());
        List<UserResponse> users = registerUsers(app.getId(), 3);
        ResourceResponse resource1 = saam.storeResource(
                new ResourceStoreRequest(app.getId(), owner.getId(), getResourceKey()));
        List<PermissionResponse> permissions1 = createPermissions(app.getId(), resource1.getKey(), users);
        ResourceResponse resource2 = saam.storeResource(
                new ResourceStoreRequest(app.getId(), owner.getId(), getResourceKey(resource1.getKey())));
        List<PermissionResponse> permissions2 = createPermissions(app.getId(), resource1.getKey(), users);

        PaginatedResult<List<PermissionResponse>> actualResult1 = saam.listPermissions(app.getId(), resource1.getKey());
        List<PermissionResponse> actualPermissions1 =
                actualResult1.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(permissions1.size(), actualPermissions1.size());
        for (PermissionResponse permission: permissions1) {
            for (PermissionResponse actualPermission: actualPermissions1) {
                if (permission.getPrincipalId().equals(actualPermission.getPrincipalId())) {
                    assertPermission(permission, actualPermission);
                }
            }
        }

        PaginatedResult<List<PermissionResponse>> actualResult2 = saam.listPermissions(app.getId(), resource2.getKey());
        List<PermissionResponse> actualPermissions2 =
                actualResult2.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(permissions1.size(), actualPermissions1.size());
        for (PermissionResponse permission: permissions2) {
            for (PermissionResponse actualPermission: actualPermissions2) {
                if (permission.getPrincipalId().equals(actualPermission.getPrincipalId())) {
                    assertPermission(permission, actualPermission);
                }
            }
        }
    }

    @Test
    public void testCheckPermission() {
        ApplicationResponse app = createApplication();
        RoleResponse owner = createRole(app.getId(), getRoleName());
        UserResponse user = registerUser(app.getId());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), owner.getId(), getResourceKey()));
        createPermission(app.getId(), resource.getKey(), user.getId());

        PermissionCheckResponse read = saam.checkPermission(
                new PermissionCheckRequest(app.getId(), resource.getKey(), user.getId(), "READ"));
        assertEquals(app.getId(), read.getApplicationId());
        assertEquals(resource.getKey(), read.getResourceKey());
        assertEquals(user.getId(), read.getPrincipalId());
        assertEquals("READ", read.getActionCode());
        assertEquals(PermissionType.ALLOW, read.getResult());

        PermissionCheckResponse edit = saam.checkPermission(
                new PermissionCheckRequest(app.getId(), resource.getKey(), user.getId(), "EDIT"));
        assertEquals(app.getId(), edit.getApplicationId());
        assertEquals(resource.getKey(), edit.getResourceKey());
        assertEquals(user.getId(), edit.getPrincipalId());
        assertEquals("EDIT", edit.getActionCode());
        assertEquals(PermissionType.ALLOW, edit.getResult());

        PermissionCheckResponse remove = saam.checkPermission(
                new PermissionCheckRequest(app.getId(), resource.getKey(), user.getId(), "REMOVE"));
        assertEquals(app.getId(), remove.getApplicationId());
        assertEquals(resource.getKey(), remove.getResourceKey());
        assertEquals(user.getId(), remove.getPrincipalId());
        assertEquals("REMOVE", remove.getActionCode());
        assertEquals(PermissionType.DENY, remove.getResult());

        PermissionCheckResponse move = saam.checkPermission(
                new PermissionCheckRequest(app.getId(), resource.getKey(), user.getId(), "MOVE"));
        assertEquals(app.getId(), move.getApplicationId());
        assertEquals(resource.getKey(), move.getResourceKey());
        assertEquals(user.getId(), move.getPrincipalId());
        assertEquals("MOVE", move.getActionCode());
        assertEquals(PermissionType.NONE, move.getResult());
    }

    @Test
    public void testCheckPermission_Owner() {
        ApplicationResponse app = createApplication();
        RoleResponse owner = createRole(app.getId(), getRoleName());
        ResourceResponse resource = saam.storeResource(
                new ResourceStoreRequest(app.getId(), owner.getId(), getResourceKey()));

        PermissionCheckResponse read = saam.checkPermission(
                new PermissionCheckRequest(app.getId(), resource.getKey(), owner.getId(), "READ"));
        assertEquals(app.getId(), read.getApplicationId());
        assertEquals(resource.getKey(), read.getResourceKey());
        assertEquals(owner.getId(), read.getPrincipalId());
        assertEquals("READ", read.getActionCode());
        assertEquals(PermissionType.ALLOW, read.getResult());

        PermissionCheckResponse edit = saam.checkPermission(
                new PermissionCheckRequest(app.getId(), resource.getKey(), owner.getId(), "EDIT"));
        assertEquals(app.getId(), edit.getApplicationId());
        assertEquals(resource.getKey(), edit.getResourceKey());
        assertEquals(owner.getId(), edit.getPrincipalId());
        assertEquals("EDIT", edit.getActionCode());
        assertEquals(PermissionType.ALLOW, edit.getResult());

        PermissionCheckResponse remove = saam.checkPermission(
                new PermissionCheckRequest(app.getId(), resource.getKey(), owner.getId(), "REMOVE"));
        assertEquals(app.getId(), remove.getApplicationId());
        assertEquals(resource.getKey(), remove.getResourceKey());
        assertEquals(owner.getId(), remove.getPrincipalId());
        assertEquals("REMOVE", remove.getActionCode());
        assertEquals(PermissionType.ALLOW, remove.getResult());

        PermissionCheckResponse move = saam.checkPermission(
                new PermissionCheckRequest(app.getId(), resource.getKey(), owner.getId(), "MOVE"));
        assertEquals(app.getId(), move.getApplicationId());
        assertEquals(resource.getKey(), move.getResourceKey());
        assertEquals(owner.getId(), move.getPrincipalId());
        assertEquals("MOVE", move.getActionCode());
        assertEquals(PermissionType.ALLOW, move.getResult());
    }

    private void assertPermission(PermissionResponse expected, PermissionResponse actual) {
        assertEquals(expected.getApplicationId(), actual.getApplicationId());
        assertEquals(expected.getResourceKey(), actual.getResourceKey());
        assertEquals(expected.getPrincipalId(), actual.getPrincipalId());
        assertEquals(expected.getActions(), actual.getActions());
        assertEquals(expected.getCreationTime(), actual.getCreationTime());
    }

    private List<PermissionResponse> createPermissions(String applicationId,
                                                       String resourceKey,
                                                       List<UserResponse> users) {
        List<PermissionResponse> permissions = new ArrayList<>(users.size());
        for (UserResponse user: users) {
            permissions.add(createPermission(
                            applicationId,
                            resourceKey,
                            user.getId()));
        }
        return permissions;
    }

    private PermissionResponse createPermission(String applicationId, String resourceKey, String principalId) {
        List<Action> actions = new LinkedList<>();
        actions.add(new Action("READ", true));
        actions.add(new Action("EDIT", true));
        actions.add(new Action("REMOVE", false));
        return saam.storePermission(
                new PermissionStoreRequest(
                        applicationId,
                        resourceKey,
                        principalId,
                        actions));
    }

    private void assertResource(ResourceResponse expected, ResourceResponse actual) {
        assertEquals(expected.getKey(), actual.getKey());
        assertEquals(expected.getApplicationId(), actual.getApplicationId());
        assertEquals(expected.getParentKey(), actual.getParentKey());
        assertEquals(expected.getOwnerId(), actual.getOwnerId());
        assertEquals(expected.getCreationTime(), actual.getCreationTime());
    }

    private List<ResourceResponse> createResources(String applicationId, String ownerId, String parentKey, int size) {
        List<ResourceResponse> resources = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String key = getResourceKey(parentKey);
            resources.add(saam.storeResource(
                    new ResourceStoreRequest(applicationId, ownerId, key)));
        }
        return resources;
    }

    private String getResourceKey() {
        return getResourceKey(null);
    }

    private String getResourceKey(String parent) {
        String key = "resource-" + new Random().nextInt(10000);
        if (null == parent) {
            return key;
        }
        return parent + ":" + key;
    }

    private void assertRole(RoleResponse expected, RoleResponse actual) {
        assertEquals(expected.getApplicationId(), actual.getApplicationId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCreationTime(), actual.getCreationTime());
    }

    private List<RoleResponse> createRoles(String applicationId, int size) {
        List<RoleResponse> roles = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String name = getRoleName();
            roles.add(saam.addRole(new RoleAddRequest(applicationId, name)));
        }
        return roles;
    }

    private List<UserSessionResponse> createUserSessions(String applicationId, String userId, int size) {
        List<UserSessionResponse> sessions = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            sessions.add(createUserSession(applicationId, userId));
        }
        return sessions;
    }

    private UserSessionResponse createUserSession(String applicationId, String userId) {
        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("key1", "value1");
        UserSessionResponse session = saam.createUserSession(
                new UserSessionCreateRequest(
                        applicationId,
                        userId,
                        sessionDetails,
                        1000));
        return session;
    }

    private void assertAPIKey(APIKeyResponse expected, APIKeyResponse actual) {
        assertEquals(expected.getApplicationId(), actual.getApplicationId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.isActive(), actual.isActive());
    }

    private UserResponse.Identifier getIdentifier(UserResponse user, IdentifierType type) {
        List<UserResponse.Identifier> identifiers = user.getIdentifiers();
        for (UserResponse.Identifier identifier: identifiers) {
            if (identifier.getType() == type) {
                return identifier;
            }
        }
        return null;
    }

    private UserResponse.OAuthIdentifier getOAuthIdentifier(UserResponse user, OAuthPlatform platform) {
        List<UserResponse.OAuthIdentifier> identifiers = user.getOAuthIdentifiers();
        for (UserResponse.OAuthIdentifier identifier: identifiers) {
            if (identifier.getPlatform() == platform) {
                return identifier;
            }
        }
        return null;
    }

    private RoleResponse createRole(String applicationId, String name) {
        RoleResponse role = saam.addRole(new RoleAddRequest(applicationId, name));
        return role;
    }

    private String getRoleName() {
        return "role-" + new Random().nextInt(10000);
    }

    private void assertUser(UserResponse expected, UserResponse actual) {
        assertEquals(expected.getApplicationId(), actual.getApplicationId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getIdentifiers(), actual.getIdentifiers());
        assertEquals(expected.getOAuthIdentifiers(), actual.getOAuthIdentifiers());
        assertEquals(expected.getRoles(), actual.getRoles());
        assertEquals(expected.getCreationTime(), actual.getCreationTime());
    }

    private void assertApplication(ApplicationResponse expected, ApplicationResponse actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCreationTime(), actual.getCreationTime());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getPasswordPolicy(), actual.getPasswordPolicy());
        assertEquals(expected.getUsernamePolicy(), actual.getUsernamePolicy());
        assertEquals(expected.getEmailPolicy(), actual.getEmailPolicy());
        assertEquals(expected.getOAuthIdentifierPolicies(), actual.getOAuthIdentifierPolicies());
    }

    private List<APIKeyResponse> createAPIKeys(String applicationId, String userId, int size) {
        List<APIKeyResponse> apiKeys = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            apiKeys.add(createAPIKey(applicationId, userId));
        }
        return apiKeys;
    }

//    private APIKeyResponse createAPIKey() {
//        ApplicationResponse app = createApplication();
//        UserResponse user = registerUser(app.getId());
//        return createAPIKey(app.getId(), user.getId());
//    }

    private APIKeyResponse createAPIKey(String applicationId, String userId) {
        String name = getAPIKeyName();
        return saam.addAPIKey(
                new APIKeyAddRequest(applicationId, userId, name));
    }

    private List<UserResponse> registerUsers(String applicationId, int size) {
        List<UserResponse> users = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            users.add(registerUser(applicationId));
        }
        return users;
    }

    private UserResponse registerUser(String applicationId) {
        Map<IdentifierType, String> identifiers = new HashMap<>();
        identifiers.put(IdentifierType.USERNAME, getUsername());
        identifiers.put(IdentifierType.EMAIL, getEmail());

        String googleOAuthIdentifier = getGoogleOAuthIdentifier();
        Map<OAuthPlatform, UserRegisterRequest.OAuthIdentifier> oAuthIdentifiers = new HashMap<>();
        oAuthIdentifiers.put(OAuthPlatform.GOOGLE,
                new UserRegisterRequest.OAuthIdentifier(googleOAuthIdentifier, Collections.emptyMap()));

        return registerUser(applicationId, identifiers, oAuthIdentifiers);
    }

    private UserResponse registerUser(String applicationId,
                                      Map<IdentifierType, String> identifiers,
                                      Map<OAuthPlatform, UserRegisterRequest.OAuthIdentifier> oAuthIdentifiers) {
        String password = "Password!";
        return saam.register(new UserRegisterRequest(
                applicationId, identifiers, oAuthIdentifiers, password, Collections.emptyList()));
    }

    private String getUsername() {
        return "username-" + new Random().nextInt(10000);
    }

    private String getEmail() {
        return "email-" + new Random().nextInt(10000) + "@foo.com";
    }

    private String getGoogleOAuthIdentifier() {
        return "google-oauth-" + new Random().nextInt(10000);
    }

    private String getAPIKeyName() {
        return "apikey-" + new Random().nextInt(10000);
    }

    private List<ApplicationResponse> createApplications(int size) {
        List<ApplicationResponse> apps = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            apps.add(createApplication());
        }
        return apps;
    }

    private ApplicationResponse createApplication() {
        String name = getApplicationName();
        return createApplication(name, true);
    }

    private ApplicationResponse createApplication(boolean isEmailVerificationRequired) {
        String name = getApplicationName();
        return createApplication(name, isEmailVerificationRequired);
    }

    private ApplicationResponse createApplication(String name, boolean isEmailVerificationRequired) {
        ApplicationResponse app = saam.addApplication(
                new ApplicationAddRequest(name, "description"));
        generatedApplications.add(app);
        app = saam.setPasswordPolicy(new PasswordPolicySetRequest(
                app.getId(), new PasswordPolicy(6, 35, true, true)));
        app = saam.addEmailPolicy(
                new EmailPolicyAddRequest(app.getId(), isEmailVerificationRequired, Collections.emptyList()));
        app = saam.addUsernamePolicy(
                new UsernamePolicyAddRequest(app.getId()));
        app = saam.addOAuthIdentifierPolicy(
                new OAuthIdentifierPolicyAddRequest(app.getId(), OAuthPlatform.GOOGLE, Collections.emptyMap()));
        app = saam.addOAuthIdentifierPolicy(
                new OAuthIdentifierPolicyAddRequest(app.getId(), OAuthPlatform.FACEBOOK, Collections.emptyMap()));
        return app;
    }

    private ApplicationResponse createSimpleApplication() {
        String name = getApplicationName();
        ApplicationResponse app = saam.addApplication(
                new ApplicationAddRequest(name, "description"));
        generatedApplications.add(app);
        return app;
    }

    private ApplicationResponse updateApplicationStatus(ApplicationResponse app, ApplicationStatus status) {
        return saam.updateApplication(new ApplicationUpdateRequest(
                app.getId(), app.getName(), app.getDescription(), status));
    }

    private String getApplicationName() {
        return "app-" + new Random().nextInt(10000);
    }

    protected abstract SAAM getSAAM();
}
