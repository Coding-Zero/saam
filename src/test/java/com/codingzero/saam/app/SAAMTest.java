package com.codingzero.saam.app;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.core.APIKey;
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
    public void testGenerateVerificationCode_WithoutPolicy() {
        ApplicationResponse app = createSimpleApplication();

        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        thrown.expect(BusinessError.class);
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, "foo@foo.com", 1000));
    }

    @Test
    public void testGenerateVerificationCode_NoIdentifierFound() {
        ApplicationResponse app = createApplication();

        UserResponse user = saam.register(new UserRegisterRequest(
                app.getId(), Collections.emptyMap(), Collections.emptyMap(), null, Collections.emptyList()));

        thrown.expect(BusinessError.class);
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, "foo@foo.com", 1000));
    }

    @Test
    public void testGenerateVerificationCode_VerificationIsNotRequired() {
        ApplicationResponse app = createApplication();

        UserResponse user = registerUser(app.getId());
        String username = getIdentifier(user, IdentifierType.USERNAME).getContent();

        thrown.expect(BusinessError.class);
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.USERNAME, username, 1000));
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
                        ApplicationStatus.DEACTIVE);
        ApplicationResponse actualApp = saam.updateApplication(request);
        assertEquals(app.getId(), actualApp.getId());
        assertEquals(newName, actualApp.getName());
        assertEquals(newDesc, actualApp.getDescription());
        assertEquals(app.getCreationTime(), actualApp.getCreationTime());
        assertEquals(ApplicationStatus.DEACTIVE, actualApp.getStatus());
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
    public void testUpdatePasswordPolicy() {
        ApplicationResponse app = createApplication();
        PasswordPolicy policy = app.getPasswordPolicy();
        app = saam.updatePasswordPolicy(new PasswordPolicyUpdateRequest(
                app.getId(), new PasswordPolicy(8, 50, false, false)));
        PasswordPolicy actualPolicy = app.getPasswordPolicy();
        assertNotEquals(policy.getMinLength(), actualPolicy.getMinLength());
        assertNotEquals(policy.getMaxLength(), actualPolicy.getMaxLength());
        assertNotEquals(policy.isNeedCapital(), actualPolicy.isNeedCapital());
        assertNotEquals(policy.isNeedSpecialChar(), actualPolicy.isNeedSpecialChar());
    }

    @Test
    public void testUpdatePasswordPolicy_NullValue() {
        ApplicationResponse app = createApplication();
        app = saam.updatePasswordPolicy(new PasswordPolicyUpdateRequest(
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
    public void testUpdateUsernamePolicy_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.updateUsernamePolicy(new UsernamePolicyUpdateRequest(app.getId(), false));
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
    public void testUpdateEmailPolicy_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.updateEmailPolicy(
                new EmailPolicyUpdateRequest(
                        app.getId(), false, Arrays.asList("foo.com"), false));
    }

    @Test
    public void testRemoveIdentifierPolicy_Username() {
        ApplicationResponse app = createApplication();
        assertNotNull(app.getUsernamePolicy());
        app = saam.removeIdentifierPolicy(app.getId(), IdentifierType.USERNAME);
        assertNull(app.getUsernamePolicy());
    }

    @Test
    public void testRemoveIdentifierPolicy_Username_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.removeIdentifierPolicy(app.getId(), IdentifierType.USERNAME);
    }

    @Test
    public void testRemoveIdentifierPolicy_Email() {
        ApplicationResponse app = createApplication();
        assertNotNull(app.getEmailPolicy());
        app = saam.removeIdentifierPolicy(app.getId(), IdentifierType.EMAIL);
        assertNull(app.getEmailPolicy());
    }

    @Test
    public void testRemoveIdentifierPolicy_Email_NotExist() {
        ApplicationResponse app = createSimpleApplication();
        thrown.expect(BusinessError.class);
        saam.removeIdentifierPolicy(app.getId(), IdentifierType.EMAIL);
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
        String roleName = getRoleName();
        RoleResponse role = createRole(app.getId(), roleName);
        UserResponse user = registerUser(app.getId());
        UserResponse actualUser = saam.updateRoles(
                new UserRoleUpdateRequest(app.getId(), user.getId(), Arrays.asList(role.getId())));
        assertEquals(1, actualUser.getRoles().size());
        assertEquals(roleName, actualUser.getRoles().get(0).getName());
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
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), 1000));
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
    public void testVerifyIdentifier_AlreadyVerified() {
        ApplicationResponse app = createApplication();
        UserResponse user = registerUser(app.getId());
        UserResponse.Identifier email = getIdentifier(user, IdentifierType.EMAIL);
        IdentifierVerificationCodeResponse code = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), 1000));
        saam.verifyIdentifier(
                new IdentifierVerifyRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), code.getCode()));

        code = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), 1000));
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
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), 1000));
        saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, email.getContent(), 1000));

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
        app = saam.updatePasswordPolicy(new PasswordPolicyUpdateRequest(
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

    private String getApplicationName() {
        return "app-" + new Random().nextInt(10000);
    }

    protected abstract SAAM getSAAM();
}
