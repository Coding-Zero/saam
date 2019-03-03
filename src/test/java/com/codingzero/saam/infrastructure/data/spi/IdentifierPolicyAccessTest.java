package com.codingzero.saam.infrastructure.data.spi;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.UsernameFormat;
import com.codingzero.saam.infrastructure.data.EmailPolicyAccess;
import com.codingzero.saam.infrastructure.data.EmailPolicyOS;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.data.UsernamePolicyAccess;
import com.codingzero.saam.infrastructure.data.UsernamePolicyOS;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class IdentifierPolicyAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierPolicyAccess access;
    private UsernamePolicyAccess usernamePolicyAccess;
    private EmailPolicyAccess emailPolicyAccess;
    private List<IdentifierPolicyOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
        usernamePolicyAccess = getUsernamePolicyAccess();
        emailPolicyAccess = getEmailPolicyAccess();
    }

    @After
    public void clean() {
        for (IdentifierPolicyOS os: generatedObjectSegments) {
            try {
                if (os.getType() == IdentifierType.USERNAME) {
                    usernamePolicyAccess.delete((UsernamePolicyOS) os);
                }
                if (os.getType() == IdentifierType.EMAIL) {
                    emailPolicyAccess.delete((EmailPolicyOS) os);
                }
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up apikey, " + os);
            }
        }
    }

    @Test
    public void testIsDuplicateType() {
        String applicationId = getApplicationId();
        UsernamePolicyOS username = createUsernamePolicyOS(applicationId);
        usernamePolicyAccess.insert(username);

        boolean isDuplicate = access.isDuplicateType(applicationId, IdentifierType.EMAIL);
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicateType_Duplicate() {
        String applicationId = getApplicationId();
        UsernamePolicyOS username = createUsernamePolicyOS(applicationId);
        usernamePolicyAccess.insert(username);

        boolean isDuplicate = access.isDuplicateType(applicationId, IdentifierType.USERNAME);
        assertTrue(isDuplicate);
    }

    @Test
    public void testSelectById() {
        String applicationId = getApplicationId();
        UsernamePolicyOS username = createUsernamePolicyOS(applicationId);
        usernamePolicyAccess.insert(username);
        EmailPolicyOS email = createEmailPolicyOS(applicationId);
        emailPolicyAccess.insert(email);

        IdentifierPolicyOS actualUsername = access.selectByType(applicationId, IdentifierType.USERNAME);
        assertOS(username, actualUsername);

        IdentifierPolicyOS actualEmail = access.selectByType(applicationId, IdentifierType.EMAIL);
        assertOS(email, actualEmail);
    }

    @Test
    public void testSelectByApplicationId() {
        String applicationId1 = getApplicationId();
        UsernamePolicyOS username1 = createUsernamePolicyOS(applicationId1);
        usernamePolicyAccess.insert(username1);
        EmailPolicyOS email1 = createEmailPolicyOS(applicationId1);
        emailPolicyAccess.insert(email1);

        String applicationId2 = getApplicationId();
        UsernamePolicyOS username2 = createUsernamePolicyOS(applicationId2);
        usernamePolicyAccess.insert(username2);

        List<IdentifierPolicyOS> actualPolicies =
                access.selectByApplicationId(applicationId1);
        assertEquals(2, actualPolicies.size());
        for (IdentifierPolicyOS actualOS: actualPolicies) {
            if (username1.getType() == actualOS.getType()) {
                assertOS(username1, actualOS);
            }
            if (email1.getType() == actualOS.getType()) {
                assertOS(email1, actualOS);
            }
        }

    }

    private void assertOS(IdentifierPolicyOS expectedOS, IdentifierPolicyOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.isVerificationRequired(), actualOS.isVerificationRequired());
        assertEquals(expectedOS.getMinLength(), actualOS.getMinLength());
        assertEquals(expectedOS.getMaxLength(), actualOS.getMaxLength());
        assertEquals(expectedOS.isActive(), actualOS.isActive());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getUpdateTime(), actualOS.getUpdateTime());
        assertEquals(expectedOS.getType(), actualOS.getType());
    }

    private EmailPolicyOS createEmailPolicyOS(String applicationId) {
        EmailPolicyOS os = new EmailPolicyOS(
                applicationId,
                true,
                5,
                255,
                true,
                new Date(),
                new Date(),
                Collections.emptyList());
        generatedObjectSegments.add(os);
        return os;
    }

    private UsernamePolicyOS createUsernamePolicyOS(String applicationId) {
        UsernamePolicyOS os = new UsernamePolicyOS(
                applicationId,
                5,
                255,
                true,
                new Date(),
                new Date(),
                UsernameFormat.URL_SAFE);
        generatedObjectSegments.add(os);
        return os;
    }

    protected abstract IdentifierPolicyAccess getAccess();
    protected abstract UsernamePolicyAccess getUsernamePolicyAccess();
    protected abstract EmailPolicyAccess getEmailPolicyAccess();
    protected abstract String getApplicationId();

}
