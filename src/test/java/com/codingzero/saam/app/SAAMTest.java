package com.codingzero.saam.app;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.infrastructure.database.ResourceOS;
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
    public void testGenerateVerificationCode() {
        ApplicationResponse app = createApplication();

        UserResponse user = saam.createUser(app.getId());
        String identifier = "foo@foo.com";
        user = saam.assignIdentifier(
                new IdentifierAssignRequest(app.getId(), user.getId(), IdentifierType.EMAIL, identifier));

        long timestamp = System.currentTimeMillis();
        IdentifierVerificationCodeResponse response = saam.generateVerificationCode(
                new IdentifierVerificationCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, identifier, 1000));

        assertNotNull(response);
        assertEquals(app.getId(), response.getApplicationId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(IdentifierType.EMAIL, response.getIdentifierType());
        assertEquals(identifier, response.getIdentifier());
        assertNotNull(response.getCode());
        assertTrue((response.getExpirationTime().getTime() - timestamp >= 1000));
    }

    @Test
    public void testGenerateResetCode() {
        ApplicationResponse app = createApplication(false);

        UserResponse user = saam.createUser(app.getId());
        String identifier = "foo@foo.com";
        user = saam.assignIdentifier(
                new IdentifierAssignRequest(app.getId(), user.getId(), IdentifierType.EMAIL, identifier));

        long timestamp = System.currentTimeMillis();
        PasswordResetCodeResponse response = saam.generateResetCode(
                new PasswordResetCodeGenerateRequest(
                        app.getId(), user.getId(), IdentifierType.EMAIL, identifier, 1000));

        assertNotNull(response);
        assertEquals(app.getId(), response.getApplicationId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(IdentifierType.EMAIL, response.getIdentifierType());
        assertEquals(identifier, response.getIdentifier());
        assertNotNull(response.getCode());
        assertTrue((response.getExpirationTime().getTime() - timestamp >= 1000));
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
