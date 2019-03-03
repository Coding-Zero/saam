package com.codingzero.saam.infrastructure.data.spi;

import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.common.IdentifierVerificationCode;
import com.codingzero.saam.infrastructure.data.IdentifierAccess;
import com.codingzero.saam.infrastructure.data.IdentifierOS;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class IdentifierAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierAccess access;
    private List<IdentifierOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (IdentifierOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up, " + os);
            }
        }
    }

    @Test
    public void testIsDuplicateContent() {
        IdentifierOS os = createObjectSegment();
        access.insert(os);
        boolean isDuplicate = access.isDuplicateContent(
                os.getKey().getApplicationId(),
                getIdentifierContent());
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicateName_Duplicate() {
        IdentifierOS os = createObjectSegment();
        access.insert(os);
        boolean isDuplicate = access.isDuplicateContent(
                os.getKey().getApplicationId(),
                os.getKey().getContent());
        assertTrue(isDuplicate);
    }

    @Test
    public void testInsert() {
        IdentifierOS os = createObjectSegment();
        access.insert(os);
        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicateContent() {
        IdentifierOS os = createObjectSegment();
        access.insert(os);
        thrown.expect(Exception.class);
        access.insert(os);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        IdentifierOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        IdentifierOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.rollback();

        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertEquals(null, actualOS);
    }

    @Test
    public void testUpdate() {
        IdentifierOS os = createObjectSegment();
        access.insert(os);

        os.setVerificationCode(
                new IdentifierVerificationCode(
                        "verification-code", new Date(System.currentTimeMillis() + 1000)));
        os.setUpdateTime(new Date());
        os.setVerified(true);
        access.update(os);

        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        IdentifierOS os = createObjectSegment();
        access.insert(os);

        os.setVerificationCode(
                new IdentifierVerificationCode(
                        "verification-code", new Date(System.currentTimeMillis() + 1000)));
        os.setUpdateTime(new Date());
        os.setVerified(true);

        manager.start();
        access.update(os);
        manager.commit();

        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        IdentifierOS os = createObjectSegment();
        access.insert(os);

        IdentifierOS originalOS = access.selectByKey(os.getKey());

        os.setVerificationCode(
                new IdentifierVerificationCode(
                        "verification-code", new Date(System.currentTimeMillis() + 1000)));
        os.setUpdateTime(new Date());
        os.setVerified(true);

        manager.start();
        access.update(os);
        manager.rollback();

        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(originalOS, actualOS);
    }

    @Test
    public void testDelete() {
        IdentifierOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);
        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        IdentifierOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        IdentifierOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        IdentifierOS actualOS = access.selectByKey(os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByType() {
        String applicationId = getApplicationId();
        List<IdentifierOS> userNames = createObjectSegments(applicationId, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames) {
            access.insert(os);
        }

        List<IdentifierOS> emails = createObjectSegments(applicationId, IdentifierType.EMAIL, 2);
        for (IdentifierOS os: emails) {
            access.insert(os);
        }

        access.deleteByType(applicationId, IdentifierType.USERNAME);

        for (IdentifierOS os: userNames) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (IdentifierOS os: emails) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByType_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        String applicationId = getApplicationId();
        List<IdentifierOS> userNames = createObjectSegments(applicationId, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames) {
            access.insert(os);
        }

        List<IdentifierOS> emails = createObjectSegments(applicationId, IdentifierType.EMAIL, 2);
        for (IdentifierOS os: emails) {
            access.insert(os);
        }

        manager.start();
        access.deleteByType(applicationId, IdentifierType.USERNAME);
        manager.commit();

        for (IdentifierOS os: userNames) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (IdentifierOS os: emails) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByType_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        String applicationId = getApplicationId();
        List<IdentifierOS> userNames = createObjectSegments(applicationId, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames) {
            access.insert(os);
        }

        List<IdentifierOS> emails = createObjectSegments(applicationId, IdentifierType.EMAIL, 2);
        for (IdentifierOS os: emails) {
            access.insert(os);
        }

        manager.start();
        access.deleteByType(applicationId, IdentifierType.USERNAME);
        manager.rollback();

        for (IdentifierOS os: userNames) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }

        for (IdentifierOS os: emails) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByTypeAndUserId() {
        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<IdentifierOS> userNames1 = createObjectSegments(applicationId, userId1, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames1) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<IdentifierOS> userNames2 = createObjectSegments(applicationId, userId2, IdentifierType.USERNAME, 2);
        for (IdentifierOS os: userNames2) {
            access.insert(os);
        }

        access.deleteByTypeAndUserId(applicationId, IdentifierType.USERNAME, userId1);

        for (IdentifierOS os: userNames1) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (IdentifierOS os: userNames2) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByTypeAndUserId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<IdentifierOS> userNames1 = createObjectSegments(applicationId, userId1, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames1) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<IdentifierOS> userNames2 = createObjectSegments(applicationId, userId2, IdentifierType.USERNAME, 2);
        for (IdentifierOS os: userNames2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByTypeAndUserId(applicationId, IdentifierType.USERNAME, userId1);
        manager.commit();

        for (IdentifierOS os: userNames1) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (IdentifierOS os: userNames2) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByTypeAndUserId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<IdentifierOS> userNames1 = createObjectSegments(applicationId, userId1, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames1) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<IdentifierOS> userNames2 = createObjectSegments(applicationId, userId2, IdentifierType.USERNAME, 2);
        for (IdentifierOS os: userNames2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByTypeAndUserId(applicationId, IdentifierType.USERNAME, userId1);
        manager.rollback();

        for (IdentifierOS os: userNames1) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }

        for (IdentifierOS os: userNames2) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<IdentifierOS> userNames = createObjectSegments(applicationId, userId1, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<IdentifierOS> emails = createObjectSegments(applicationId, userId2, IdentifierType.EMAIL, 2);
        for (IdentifierOS os: emails) {
            access.insert(os);
        }

        access.deleteByApplicationId(applicationId);

        for (IdentifierOS os: userNames) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (IdentifierOS os: emails) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<IdentifierOS> userNames = createObjectSegments(applicationId, userId1, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<IdentifierOS> emails = createObjectSegments(applicationId, userId2, IdentifierType.EMAIL, 2);
        for (IdentifierOS os: emails) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.commit();

        for (IdentifierOS os: userNames) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }

        for (IdentifierOS os: emails) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertNull(actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("identifier-access", access);

        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<IdentifierOS> userNames = createObjectSegments(applicationId, userId1, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<IdentifierOS> emails = createObjectSegments(applicationId, userId2, IdentifierType.EMAIL, 2);
        for (IdentifierOS os: emails) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.rollback();

        for (IdentifierOS os: userNames) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }

        for (IdentifierOS os: emails) {
            IdentifierOS actualOS = access.selectByKey(os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testSelectByTypeAndUserId() {
        String applicationId = getApplicationId();
        String userId1 = getUserId(applicationId);
        List<IdentifierOS> userNames1 = createObjectSegments(applicationId, userId1, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames1) {
            access.insert(os);
        }

        String userId2 = getUserId(applicationId);
        List<IdentifierOS> userNames2 = createObjectSegments(applicationId, userId2, IdentifierType.USERNAME, 2);
        for (IdentifierOS os: userNames2) {
            access.insert(os);
        }


        List<IdentifierOS> actualUserNames1 =
                access.selectByUserId(applicationId, userId1);
        assertEquals(3, actualUserNames1.size());
        for (IdentifierOS os: userNames1) {
            for (IdentifierOS actualOS: actualUserNames1) {
                if (os.getKey().getContent().equalsIgnoreCase(actualOS.getKey().getContent())) {
                    assertOS(os, actualOS);
                }
            }
        }

        List<IdentifierOS> actualUserNames2 =
                access.selectByUserId(applicationId, userId2);
        assertEquals(2, actualUserNames2.size());
        for (IdentifierOS os: userNames2) {
            for (IdentifierOS actualOS: actualUserNames2) {
                if (os.getKey().getContent().equalsIgnoreCase(actualOS.getKey().getContent())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    @Test
    public void testSelectByType() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        List<IdentifierOS> userNames = createObjectSegments(applicationId, userId, IdentifierType.USERNAME, 3);
        for (IdentifierOS os: userNames) {
            access.insert(os);
        }

        List<IdentifierOS> emails = createObjectSegments(applicationId, userId, IdentifierType.EMAIL, 2);
        for (IdentifierOS os: emails) {
            access.insert(os);
        }


        PaginatedResult<List<IdentifierOS>> userNamesResult =
                access.selectByType(applicationId, IdentifierType.USERNAME);
        List<IdentifierOS> actualUserNames =
                userNamesResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(3, actualUserNames.size());
        for (IdentifierOS os: userNames) {
            for (IdentifierOS actualOS: actualUserNames) {
                if (os.getKey().getContent().equalsIgnoreCase(actualOS.getKey().getContent())) {
                    assertOS(os, actualOS);
                }
            }
        }

        PaginatedResult<List<IdentifierOS>> emailsResult =
                access.selectByType(applicationId, IdentifierType.EMAIL);
        List<IdentifierOS> actualEmails =
                emailsResult.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(2, actualEmails.size());
        for (IdentifierOS os: emails) {
            for (IdentifierOS actualOS: actualEmails) {
                if (os.getKey().getContent().equalsIgnoreCase(actualOS.getKey().getContent())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    private String getIdentifierContent() {
        return "identifier-" + new Random().nextInt(99999);
    }

    private List<IdentifierOS> createObjectSegments(String applicationId, IdentifierType type, int size) {
        List<IdentifierOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String content = getIdentifierContent();
            String userId = getUserId(applicationId);
            osList.add(createObjectSegment(applicationId, userId, type, content));
        }
        return osList;
    }

    private List<IdentifierOS> createObjectSegments(String applicationId, String userId, IdentifierType type, int size) {
        List<IdentifierOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String content = getIdentifierContent();
            osList.add(createObjectSegment(applicationId, userId, type, content));
        }
        return osList;
    }

    private IdentifierOS createObjectSegment() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        String content  = getIdentifierContent();
        IdentifierType type = IdentifierType.USERNAME;
        return createObjectSegment(applicationId, userId, type, content);
    }

    private IdentifierOS createObjectSegment(String applicationId, String userId,
                                             IdentifierType type, String content) {
        IdentifierOS os = new IdentifierOS(
                new IdentifierKey(applicationId, content),
                type,
                userId,
                true,
                null,
                new Date(),
                new Date());
        generatedObjectSegments.add(os);
        return os;
    }

    private void assertOS(IdentifierOS expectedOS, IdentifierOS actualOS) {
        assertEquals(expectedOS.getKey().getApplicationId(), actualOS.getKey().getApplicationId());
        assertEquals(expectedOS.getType(), actualOS.getType());
        assertEquals(expectedOS.getKey().getContent(), actualOS.getKey().getContent());
        assertEquals(expectedOS.getUserId(), actualOS.getUserId());
        assertEquals(expectedOS.isVerified(), actualOS.isVerified());
        assertEquals(expectedOS.getVerificationCode(), actualOS.getVerificationCode());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getUpdateTime(), actualOS.getUpdateTime());
    }

    protected abstract IdentifierAccess getAccess();
    protected abstract String getApplicationId();
    protected abstract String getUserId(String applicationId);

}
