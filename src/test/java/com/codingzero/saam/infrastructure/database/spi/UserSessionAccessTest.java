package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.UserSessionAccess;
import com.codingzero.saam.infrastructure.database.UserSessionOS;
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class UserSessionAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserSessionAccess access;
    private List<UserSessionOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (UserSessionOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up apikey, " + os);
            }
        }
    }

    @Test
    public void testGenerateKey() {
        String applicationId = getApplicationId();
        int size = 10;
        Set<String> keys = new HashSet<>(size);
        for (int i = 0; i < size; i ++) {
            keys.add(access.generateKey(applicationId).toLowerCase());
        }
        assertEquals(size, keys.size());
    }

    @Test
    public void testInsert() {
        UserSessionOS os = createObjectSegment();
        access.insert(os);

        UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicateKey() {
        String applicationId = getApplicationId();
        String key = getKey(applicationId);
        String userId1 = getPrincipalId(applicationId, PrincipalType.USER);
        UserSessionOS os1 = createObjectSegment(applicationId, key, userId1);
        access.insert(os1);

        String userId2 = getPrincipalId(applicationId, PrincipalType.USER);
        UserSessionOS os2 = createObjectSegment(applicationId, key, userId2);
        thrown.expect(Exception.class);
        access.insert(os2);
    }

    @Test
    public void testInsert_DifferentApplicationId() {
        String applicationId1 = getApplicationId();
        String key = getKey(applicationId1);
        String userId = getPrincipalId(applicationId1, PrincipalType.USER);
        UserSessionOS os = createObjectSegment(applicationId1, key, userId);
        access.insert(os);

        String applicationId2 = getApplicationId();
        String userId2 = getPrincipalId(applicationId1, PrincipalType.USER);
        UserSessionOS os2 = createObjectSegment(applicationId2, key, userId2);
        access.insert(os2);

        UserSessionOS actualOS = access.selectByKey(applicationId1, key);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("user-access", access);

        UserSessionOS os = createObjectSegment();

        manager.start();
        access.insert(os);
        manager.commit();

        UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("user-access", access);

        UserSessionOS os = createObjectSegment();

        manager.start();
        access.insert(os);
        manager.rollback();

        UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete() {
        UserSessionOS os = createObjectSegment();
        access.insert(os);

        access.delete(os);

        UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("user-access", access);

        UserSessionOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("user-access", access);

        UserSessionOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByUserId() {
        String applicationId = getApplicationId();
        String userId1 = getPrincipalId(applicationId, PrincipalType.USER);
        List<UserSessionOS> osList1 = createObjectSegments(applicationId, userId1, 3);
        for (UserSessionOS os: osList1) {
            access.insert(os);
        }
        String userId2 = getPrincipalId(applicationId, PrincipalType.USER);
        List<UserSessionOS> osList2 = createObjectSegments(applicationId, userId2, 2);
        for (UserSessionOS os: osList2) {
            access.insert(os);
        }

        access.deleteByUserId(applicationId, userId1);

        for (UserSessionOS os: osList1) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertNull(actualOS);
        }
        for (UserSessionOS os: osList2) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByUserId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId = getApplicationId();
        String userId1 = getPrincipalId(applicationId, PrincipalType.USER);
        List<UserSessionOS> osList1 = createObjectSegments(applicationId, userId1, 3);
        for (UserSessionOS os: osList1) {
            access.insert(os);
        }
        String userId2 = getPrincipalId(applicationId, PrincipalType.USER);
        List<UserSessionOS> osList2 = createObjectSegments(applicationId, userId2, 2);
        for (UserSessionOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByUserId(applicationId, userId1);
        manager.commit();

        for (UserSessionOS os: osList1) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertNull(actualOS);
        }
        for (UserSessionOS os: osList2) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByUserId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId = getApplicationId();
        String userId1 = getPrincipalId(applicationId, PrincipalType.USER);
        List<UserSessionOS> osList1 = createObjectSegments(applicationId, userId1, 3);
        for (UserSessionOS os: osList1) {
            access.insert(os);
        }
        String userId2 = getPrincipalId(applicationId, PrincipalType.USER);
        List<UserSessionOS> osList2 = createObjectSegments(applicationId, userId2, 2);
        for (UserSessionOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByUserId(applicationId, userId1);
        manager.rollback();

        for (UserSessionOS os: osList1) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
        for (UserSessionOS os: osList2) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId1 = getApplicationId();
        String userId1 = getPrincipalId(applicationId1, PrincipalType.USER);
        List<UserSessionOS> osList1 = createObjectSegments(applicationId1, userId1, 3);
        for (UserSessionOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        String userId2 = getPrincipalId(applicationId2, PrincipalType.USER);
        List<UserSessionOS> osList2 = createObjectSegments(applicationId2, userId2, 2);
        for (UserSessionOS os: osList2) {
            access.insert(os);
        }

        access.deleteByApplicationId(applicationId1);

        for (UserSessionOS os: osList1) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertNull(actualOS);
        }
        for (UserSessionOS os: osList2) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId1 = getApplicationId();
        String userId1 = getPrincipalId(applicationId1, PrincipalType.USER);
        List<UserSessionOS> osList1 = createObjectSegments(applicationId1, userId1, 3);
        for (UserSessionOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        String userId2 = getPrincipalId(applicationId2, PrincipalType.USER);
        List<UserSessionOS> osList2 = createObjectSegments(applicationId2, userId2, 2);
        for (UserSessionOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.commit();

        for (UserSessionOS os: osList1) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertNull(actualOS);
        }
        for (UserSessionOS os: osList2) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId1 = getApplicationId();
        String userId1 = getPrincipalId(applicationId1, PrincipalType.USER);
        List<UserSessionOS> osList1 = createObjectSegments(applicationId1, userId1, 3);
        for (UserSessionOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        String userId2 = getPrincipalId(applicationId2, PrincipalType.USER);
        List<UserSessionOS> osList2 = createObjectSegments(applicationId2, userId2, 2);
        for (UserSessionOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.rollback();

        for (UserSessionOS os: osList1) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
        for (UserSessionOS os: osList2) {
            UserSessionOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testSelectByUserId() {
        String applicationId = getApplicationId();
        String userId1 = getPrincipalId(applicationId, PrincipalType.USER);
        List<UserSessionOS> osList1 = createObjectSegments(applicationId, userId1, 3);
        for (UserSessionOS os: osList1) {
            access.insert(os);
        }
        String userId2 = getPrincipalId(applicationId, PrincipalType.USER);
        List<UserSessionOS> osList2 = createObjectSegments(applicationId, userId2, 2);
        for (UserSessionOS os: osList2) {
            access.insert(os);
        }

        PaginatedResult<List<UserSessionOS>> actualOSListResult = access.selectByUserId(applicationId, userId1);
        List<UserSessionOS> actualOSList =
                actualOSListResult.start(new OffsetBasedResultPage(1, 10)).getResult();

        assertEquals(3, actualOSList.size());
        for (UserSessionOS os: osList1) {
            for (UserSessionOS actualOS: actualOSList) {
                if (os.getKey().equalsIgnoreCase(actualOS.getKey())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    private void assertOS(UserSessionOS expectedOS, UserSessionOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.getKey(), actualOS.getKey());
        assertEquals(expectedOS.getUserId(), actualOS.getUserId());
        assertEquals(expectedOS.getExpirationTime(), actualOS.getExpirationTime());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getDetails(), actualOS.getDetails());
    }

    private List<UserSessionOS> createObjectSegments(String applicationId, String userId, int size) {
        List<UserSessionOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String key = getKey(applicationId);
            osList.add(createObjectSegment(applicationId, key, userId));
        }
        return osList;
    }

    private UserSessionOS createObjectSegment() {
        String applicationId = getApplicationId();
        String key = getKey(applicationId);
        String userId = getPrincipalId(applicationId, PrincipalType.USER);
        return createObjectSegment(applicationId, key, userId);
    }

    private UserSessionOS createObjectSegment(String applicationId, String key, String userId) {
        UserSessionOS os = new UserSessionOS(
                applicationId,
                key,
                userId,
                new Date(System.currentTimeMillis() + 1000),
                new Date(),
                Collections.emptyMap());
        generatedObjectSegments.add(os);
        return os;
    }

    protected abstract UserSessionAccess getAccess();
    protected abstract String getApplicationId();
    protected abstract String getKey(String applicationId);
    protected abstract String getPrincipalId(String applicationId, PrincipalType type);

}
