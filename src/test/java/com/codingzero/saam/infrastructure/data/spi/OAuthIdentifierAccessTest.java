package com.codingzero.saam.infrastructure.data.spi;

import com.codingzero.saam.common.OAuthIdentifierKey;
import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierOS;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
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
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class OAuthIdentifierAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthIdentifierAccess access;
    private List<OAuthIdentifierOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (OAuthIdentifierOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up, " + os);
            }
        }
    }

    @Test
    public void testIsDuplicateContent() {
        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);
        boolean isDuplicate = access.isDuplicateKey(os.getKey());
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicateName_Duplicate() {
        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);
        boolean isDuplicate = access.isDuplicateKey(os.getKey());
        assertTrue(isDuplicate);
    }

    @Test
    public void testInsert() {
        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);
        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicateContent() {
        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);
        thrown.expect(Exception.class);
        access.insert(os);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        OAuthIdentifierOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        OAuthIdentifierOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.rollback();

        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertEquals(null, actualOS);
    }

    @Test
    public void testUpdate() {
        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);

        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        os.setProperties(properties);
        os.setUpdateTime(new Date());
        access.update(os);

        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);

        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        os.setProperties(properties);
        os.setUpdateTime(new Date());

        manager.start();
        access.update(os);
        manager.commit();

        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);

        OAuthIdentifierOS originalOS = access.selectByKey(os.getKey());

        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        os.setProperties(properties);
        os.setUpdateTime(new Date());

        manager.start();
        access.update(os);
        manager.rollback();

        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(originalOS, actualOS);
    }

    @Test
    public void testDelete() {
        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);
        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        OAuthIdentifierOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByPlatform() {
        String applicationId = getApplicationId();
        List<OAuthIdentifierOS> googleIds = createObjectSegments(applicationId, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds) {
            access.insert(os);
        }

        List<OAuthIdentifierOS> facebookIds = createObjectSegments(applicationId, OAuthPlatform.FACEBOOK, 2);
        for (OAuthIdentifierOS os: facebookIds) {
            access.insert(os);
        }

        access.deleteByPlatform(applicationId, OAuthPlatform.GOOGLE);

        for (OAuthIdentifierOS os: googleIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (OAuthIdentifierOS os: facebookIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByPlatform_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        String applicationId = getApplicationId();
        List<OAuthIdentifierOS> googleIds = createObjectSegments(applicationId, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds) {
            access.insert(os);
        }

        List<OAuthIdentifierOS> facebookIds = createObjectSegments(applicationId, OAuthPlatform.FACEBOOK, 2);
        for (OAuthIdentifierOS os: facebookIds) {
            access.insert(os);
        }

        manager.start();
        access.deleteByPlatform(applicationId, OAuthPlatform.GOOGLE);
        manager.commit();

        for (OAuthIdentifierOS os: googleIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (OAuthIdentifierOS os: facebookIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByPlatform_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        String applicationId = getApplicationId();
        List<OAuthIdentifierOS> googleIds = createObjectSegments(applicationId, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds) {
            access.insert(os);
        }

        List<OAuthIdentifierOS> facebookIds = createObjectSegments(applicationId, OAuthPlatform.FACEBOOK, 2);
        for (OAuthIdentifierOS os: facebookIds) {
            access.insert(os);
        }

        manager.start();
        access.deleteByPlatform(applicationId, OAuthPlatform.GOOGLE);
        manager.rollback();

        for (OAuthIdentifierOS os: googleIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }

        for (OAuthIdentifierOS os: facebookIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByUserId() {
        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds1 = createObjectSegments(applicationId, userId1, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds1) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds2 = createObjectSegments(applicationId, userId2, OAuthPlatform.GOOGLE, 2);
        for (OAuthIdentifierOS os: googleIds2) {
            access.insert(os);
        }

        access.deleteByUserId(applicationId, userId1);

        for (OAuthIdentifierOS os: googleIds1) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (OAuthIdentifierOS os: googleIds2) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByUserId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds1 = createObjectSegments(applicationId, userId1, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds1) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds2 = createObjectSegments(applicationId, userId2, OAuthPlatform.GOOGLE, 2);
        for (OAuthIdentifierOS os: googleIds2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByUserId(applicationId, userId1);
        manager.commit();

        for (OAuthIdentifierOS os: googleIds1) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (OAuthIdentifierOS os: googleIds2) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByUserId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds1 = createObjectSegments(applicationId, userId1, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds1) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds2 = createObjectSegments(applicationId, userId2, OAuthPlatform.GOOGLE, 2);
        for (OAuthIdentifierOS os: googleIds2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByUserId(applicationId, userId1);
        manager.rollback();

        for (OAuthIdentifierOS os: googleIds1) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }

        for (OAuthIdentifierOS os: googleIds2) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds = createObjectSegments(applicationId, userId1, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<OAuthIdentifierOS> facebookIds = createObjectSegments(applicationId, userId2, OAuthPlatform.FACEBOOK, 2);
        for (OAuthIdentifierOS os: facebookIds) {
            access.insert(os);
        }

        access.deleteByApplicationId(applicationId);

        for (OAuthIdentifierOS os: googleIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (OAuthIdentifierOS os: facebookIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds = createObjectSegments(applicationId, userId1, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<OAuthIdentifierOS> facebookIds = createObjectSegments(applicationId, userId2, OAuthPlatform.FACEBOOK, 2);
        for (OAuthIdentifierOS os: facebookIds) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.commit();

        for (OAuthIdentifierOS os: googleIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (OAuthIdentifierOS os: facebookIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("oauth-identifier-access", access);

        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds = createObjectSegments(applicationId, userId1, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<OAuthIdentifierOS> facebookIds = createObjectSegments(applicationId, userId2, OAuthPlatform.FACEBOOK, 2);
        for (OAuthIdentifierOS os: facebookIds) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.rollback();

        for (OAuthIdentifierOS os: googleIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }

        for (OAuthIdentifierOS os: facebookIds) {
            OAuthIdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testSelectByTypeAndUserId() {
        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds1 = createObjectSegments(applicationId, userId1, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds1) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<OAuthIdentifierOS> googleId2 = createObjectSegments(applicationId, userId2, OAuthPlatform.GOOGLE, 2);
        for (OAuthIdentifierOS os: googleId2) {
            access.insert(os);
        }


        List<OAuthIdentifierOS> actualGoogleIds1 =
                access.selectByUserId(applicationId, userId1);
        assertEquals(3, actualGoogleIds1.size());
        for (OAuthIdentifierOS os: googleIds1) {
            for (OAuthIdentifierOS actualOS: actualGoogleIds1) {
                if (os.getKey().getContent().equalsIgnoreCase(actualOS.getKey().getContent())) {
                    assertOS(os, actualOS);
                }
            }
        }

        List<OAuthIdentifierOS> actualGoogleIds2 =
                access.selectByUserId(applicationId, userId2);
        assertEquals(2, actualGoogleIds2.size());
        for (OAuthIdentifierOS os: googleId2) {
            for (OAuthIdentifierOS actualOS: actualGoogleIds2) {
                if (os.getKey().getContent().equalsIgnoreCase(actualOS.getKey().getContent())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    @Test
    public void testSelectByPlatform() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        List<OAuthIdentifierOS> googleIds = createObjectSegments(applicationId, userId, OAuthPlatform.GOOGLE, 3);
        for (OAuthIdentifierOS os: googleIds) {
            access.insert(os);
        }

        List<OAuthIdentifierOS> facebookIds = createObjectSegments(applicationId, userId, OAuthPlatform.FACEBOOK, 2);
        for (OAuthIdentifierOS os: facebookIds) {
            access.insert(os);
        }


        PaginatedResult<List<OAuthIdentifierOS>> googleIdsResult =
                access.selectByPlatform(applicationId, OAuthPlatform.GOOGLE);
        List<OAuthIdentifierOS> actualGoogleIds =
                googleIdsResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(3, actualGoogleIds.size());
        for (OAuthIdentifierOS os: googleIds) {
            for (OAuthIdentifierOS actualOS: actualGoogleIds) {
                if (os.getKey().getContent().equalsIgnoreCase(actualOS.getKey().getContent())) {
                    assertOS(os, actualOS);
                }
            }
        }

        PaginatedResult<List<OAuthIdentifierOS>> facebookIdsResult =
                access.selectByPlatform(applicationId, OAuthPlatform.FACEBOOK);
        List<OAuthIdentifierOS> actualFacebookIds =
                facebookIdsResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(2, actualFacebookIds.size());
        for (OAuthIdentifierOS os: facebookIds) {
            for (OAuthIdentifierOS actualOS: actualFacebookIds) {
                if (os.getKey().getContent().equalsIgnoreCase(actualOS.getKey().getContent())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    private String getIdentifierContent() {
        return "oauth-identifier-" + new Random().nextInt(99999);
    }

    private List<OAuthIdentifierOS> createObjectSegments(String applicationId, OAuthPlatform platform, int size) {
        List<OAuthIdentifierOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String content = getIdentifierContent();
            String userId = getUserId(applicationId);
            osList.add(createObjectSegment(applicationId, userId, platform, content));
        }
        return osList;
    }

    private List<OAuthIdentifierOS> createObjectSegments(String applicationId, String userId,
                                                    OAuthPlatform platform, int size) {
        List<OAuthIdentifierOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String content = getIdentifierContent();
            osList.add(createObjectSegment(applicationId, userId, platform, content));
        }
        return osList;
    }

    private OAuthIdentifierOS createObjectSegment() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        String content  = getIdentifierContent();
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        return createObjectSegment(applicationId, userId, platform, content);
    }

    private OAuthIdentifierOS createObjectSegment(String applicationId, String userId,
                                                  OAuthPlatform platform, String content) {
        OAuthIdentifierOS os = new OAuthIdentifierOS(
                new OAuthIdentifierKey(applicationId, platform, content),
                userId,
                Collections.emptyMap(),
                new Date(),
                new Date());
        generatedObjectSegments.add(os);
        return os;
    }

    private void assertOS(OAuthIdentifierOS expectedOS, OAuthIdentifierOS actualOS) {
        assertEquals(expectedOS.getKey().getApplicationId(), actualOS.getKey().getApplicationId());
        assertEquals(expectedOS.getKey().getPlatform(), actualOS.getKey().getPlatform());
        assertEquals(expectedOS.getKey().getContent(), actualOS.getKey().getContent());
        assertEquals(expectedOS.getUserId(), actualOS.getUserId());
        assertEquals(expectedOS.getProperties(), actualOS.getProperties());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getUpdateTime(), actualOS.getUpdateTime());
    }

    protected abstract OAuthIdentifierAccess getAccess();
    protected abstract String getApplicationId();
    protected abstract String getUserId(String applicationId);

}
