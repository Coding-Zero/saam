package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyOS;
import com.codingzero.utilities.transaction.TransactionManager;
import com.codingzero.utilities.transaction.TransactionManagerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class OAuthIdentifierPolicyAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthIdentifierPolicyAccess access;
    private List<OAuthIdentifierPolicyOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (OAuthIdentifierPolicyOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("sometime wrong during clean up, " + os);
            }
        }
    }

    @Test
    public void testIsDuplicatePlatform() {
        String applicationId = getApplicationId();
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        boolean isDuplicate = access.isDuplicatePlatform(applicationId, platform);
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicatePlatform_Duplicate() {
        String applicationId = getApplicationId();
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        OAuthIdentifierPolicyOS os = createObjectSegment(applicationId, platform);
        access.insert(os);
        boolean isDuplicate = access.isDuplicatePlatform(applicationId, platform);
        assertTrue(isDuplicate);
    }

    @Test
    public void testInsert() {
        OAuthIdentifierPolicyOS os = createObjectSegment();
        access.insert(os);
        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicatePlatform() {
        String applicationId = getApplicationId();
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        OAuthIdentifierPolicyOS os = createObjectSegment(applicationId, platform);
        access.insert(os);
        OAuthIdentifierPolicyOS os2 = createObjectSegment(applicationId, platform);
        thrown.expect(Exception.class);
        access.insert(os2);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-policy-access", access);

        OAuthIdentifierPolicyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        OAuthIdentifierPolicyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.rollback();

        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertNull(actualOS);
    }

    @Test
    public void testUpdate() {
        OAuthIdentifierPolicyOS os = createObjectSegment();
        access.insert(os);

        Map<String, Object> configurations = new HashMap<>();
        configurations.put("key1", "value1");
        os.setConfigurations(configurations);
        os.setUpdateTime(new Date());
        os.setActive(false);

        access.update(os);
        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        OAuthIdentifierPolicyOS os = createObjectSegment();
        access.insert(os);

        Map<String, Object> configurations = new HashMap<>();
        configurations.put("key1", "value1");
        os.setConfigurations(configurations);
        os.setUpdateTime(new Date());
        os.setActive(false);

        manager.start();
        access.update(os);
        manager.commit();

        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        OAuthIdentifierPolicyOS os = createObjectSegment();
        access.insert(os);

        OAuthIdentifierPolicyOS os2 = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);

        Map<String, Object> configurations = new HashMap<>();
        configurations.put("key1", "value1");
        os.setConfigurations(configurations);
        os.setUpdateTime(new Date());
        os.setActive(false);

        manager.start();
        access.update(os);
        manager.rollback();

        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertOS(os2, actualOS);
    }

    @Test
    public void testDelete() {
        OAuthIdentifierPolicyOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);
        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        OAuthIdentifierPolicyOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        OAuthIdentifierPolicyOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        OAuthIdentifierPolicyOS actualOS = access.selectByPlatform(os.getApplicationId(), OAuthPlatform.GOOGLE);
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId = getApplicationId();
        OAuthIdentifierPolicyOS googleIdPolicy = createObjectSegment(applicationId, OAuthPlatform.GOOGLE);
        access.insert(googleIdPolicy);
        OAuthIdentifierPolicyOS facebookIdPolicy = createObjectSegment(applicationId, OAuthPlatform.FACEBOOK);
        access.insert(facebookIdPolicy);

        access.deleteByApplicationId(applicationId);

        OAuthIdentifierPolicyOS actualGoogleIdPolicy = access.selectByPlatform(applicationId, OAuthPlatform.GOOGLE);
        assertNull(actualGoogleIdPolicy);
        OAuthIdentifierPolicyOS actualFacebookIdPolicy = access.selectByPlatform(applicationId, OAuthPlatform.FACEBOOK);
        assertNull(actualFacebookIdPolicy);

    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        String applicationId = getApplicationId();
        OAuthIdentifierPolicyOS googleIdPolicy = createObjectSegment(applicationId, OAuthPlatform.GOOGLE);
        access.insert(googleIdPolicy);
        OAuthIdentifierPolicyOS facebookIdPolicy = createObjectSegment(applicationId, OAuthPlatform.FACEBOOK);
        access.insert(facebookIdPolicy);

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.commit();

        OAuthIdentifierPolicyOS actualGoogleIdPolicy = access.selectByPlatform(applicationId, OAuthPlatform.GOOGLE);
        assertNull(actualGoogleIdPolicy);
        OAuthIdentifierPolicyOS actualFacebookIdPolicy = access.selectByPlatform(applicationId, OAuthPlatform.FACEBOOK);
        assertNull(actualFacebookIdPolicy);
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        String applicationId = getApplicationId();
        OAuthIdentifierPolicyOS googleIdPolicy = createObjectSegment(applicationId, OAuthPlatform.GOOGLE);
        access.insert(googleIdPolicy);
        OAuthIdentifierPolicyOS facebookIdPolicy = createObjectSegment(applicationId, OAuthPlatform.FACEBOOK);
        access.insert(facebookIdPolicy);

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.rollback();

        OAuthIdentifierPolicyOS actualGoogleIdPolicy = access.selectByPlatform(applicationId, OAuthPlatform.GOOGLE);
        assertOS(googleIdPolicy, actualGoogleIdPolicy);
        OAuthIdentifierPolicyOS actualFacebookIdPolicy = access.selectByPlatform(applicationId, OAuthPlatform.FACEBOOK);
        assertOS(facebookIdPolicy, actualFacebookIdPolicy);
    }

    @Test
    public void testSelectByApplicationId() {
        String applicationId = getApplicationId();
        OAuthIdentifierPolicyOS googleIdPolicy = createObjectSegment(applicationId, OAuthPlatform.GOOGLE);
        access.insert(googleIdPolicy);
        OAuthIdentifierPolicyOS facebookIdPolicy = createObjectSegment(applicationId, OAuthPlatform.FACEBOOK);
        access.insert(facebookIdPolicy);

        List<OAuthIdentifierPolicyOS> actualOSList = access.selectByApplicationId(applicationId);
        assertEquals(2, actualOSList.size());
        for (OAuthIdentifierPolicyOS actualOS: actualOSList) {
            if (actualOS.getPlatform() == googleIdPolicy.getPlatform()) {
                assertOS(googleIdPolicy, actualOS);
            }
            if (actualOS.getPlatform() == facebookIdPolicy.getPlatform()) {
                assertOS(facebookIdPolicy, actualOS);
            }
        }

    }

    private void assertOS(OAuthIdentifierPolicyOS expectedOS, OAuthIdentifierPolicyOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.getPlatform(), actualOS.getPlatform());
        assertEquals(expectedOS.getConfigurations(), actualOS.getConfigurations());
        assertEquals(expectedOS.isActive(), actualOS.isActive());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getUpdateTime(), actualOS.getUpdateTime());
    }

    private List<OAuthIdentifierPolicyOS> createObjectSegments(String applicationId, OAuthPlatform platform, int size) {
        List<OAuthIdentifierPolicyOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            osList.add(createObjectSegment(applicationId, platform));
        }
        return osList;
    }

    private OAuthIdentifierPolicyOS createObjectSegment() {
        String applicationId = getApplicationId();
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        return createObjectSegment(applicationId, platform);
    }

    private OAuthIdentifierPolicyOS createObjectSegment(String applicationId, OAuthPlatform platform) {
        OAuthIdentifierPolicyOS os = new OAuthIdentifierPolicyOS(
                applicationId,
                platform,
                Collections.emptyMap(),
                true,
                new Date(),
                new Date());
        generatedObjectSegments.add(os);
        return os;
    }

    protected abstract OAuthIdentifierPolicyAccess getAccess();
    protected abstract String getApplicationId();

}
