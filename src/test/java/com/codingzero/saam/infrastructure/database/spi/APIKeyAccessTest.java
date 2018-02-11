package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionManager;
import com.codingzero.utilities.transaction.TransactionManagerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public abstract class APIKeyAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private APIKeyAccess access;
    private List<APIKeyOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (APIKeyOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up apikey, " + os);
            }
        }
    }

    @Test
    public void testInsert() {
        APIKeyOS os = createObjectSegment();
        access.insert(os);

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicateKey() {
        APIKeyOS os = createObjectSegment();
        access.insert(os);
        thrown.expect(Exception.class);
        access.insert(os);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        APIKeyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        APIKeyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.rollback();

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertEquals(null, actualOS);
    }

    @Test
    public void testInsert_DuplicateKey_Transaction() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        APIKeyOS os = createObjectSegment();
        manager.start();
        try {
            access.insert(os);
            access.insert(os);
            manager.commit();
        } catch (Exception e) {
            manager.rollback();
        }
        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertEquals(null, actualOS);
    }

    @Test
    public void testUpdate() {
        String newName = generateName();
        APIKeyOS os = createObjectSegment();
        access.insert(os);

        os.setName(newName);
        access.update(os);

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertEquals(newName, actualOS.getName());
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        String newName = generateName();
        APIKeyOS os = createObjectSegment();

        access.insert(os);
        manager.start();
        os.setName(newName);
        access.update(os);
        manager.commit();

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertEquals(newName, actualOS.getName());
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        String newName = generateName();
        APIKeyOS os = createObjectSegment();

        access.insert(os);
        manager.start();
        os.setName(newName);
        access.update(os);
        manager.rollback();

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertNotEquals(newName, actualOS.getName());
    }

    @Test
    public void testDelete() {
        APIKeyOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertEquals(null, actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        APIKeyOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertEquals(null, actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        APIKeyOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByUserId() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        for (int i = 0; i < 3; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId);
            access.insert(os);
        }
        access.deleteByUserId(applicationId, userId);
        List<APIKeyOS> actualOSList = access.selectByUserId(applicationId, userId);
        assertEquals(0, actualOSList.size());
    }

    @Test
    public void testDeleteByUserId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        for (int i = 0; i < 3; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId);
            access.insert(os);
        }
        manager.start();
        access.deleteByUserId(applicationId, userId);
        manager.commit();
        List<APIKeyOS> actualOSList = access.selectByUserId(applicationId, userId);
        assertEquals(0, actualOSList.size());
    }

    @Test
    public void testDeleteByUserId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        for (int i = 0; i < 3; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId);
            access.insert(os);
        }
        manager.start();
        access.deleteByUserId(applicationId, userId);
        manager.rollback();
        List<APIKeyOS> actualOSList = access.selectByUserId(applicationId, userId);
        assertEquals(3, actualOSList.size());
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        for (int i = 0; i < 3; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId);
            access.insert(os);
        }
        access.deleteByApplicationId(applicationId);
        List<APIKeyOS> actualOSList = access.selectByUserId(applicationId, userId);
        assertEquals(0, actualOSList.size());
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        for (int i = 0; i < 3; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId);
            access.insert(os);
        }
        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.commit();
        List<APIKeyOS> actualOSList = access.selectByUserId(applicationId, userId);
        assertEquals(0, actualOSList.size());
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        for (int i = 0; i < 3; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId);
            access.insert(os);
        }
        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.rollback();
        List<APIKeyOS> actualOSList = access.selectByUserId(applicationId, userId);
        assertEquals(3, actualOSList.size());
    }

    @Test
    public void testSelectByKey() {
        APIKeyOS os = createObjectSegment();
        access.insert(os);
        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testSelectByPrincipalOS() {
        APIKeyOS os = createObjectSegment();
        access.insert(os);
        APIKeyOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testSelectByUserId() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        for (int i = 0; i < 3; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId);
            access.insert(os);
        }
        String userId2 = getUserId(applicationId);
        for (int i = 0; i < 2; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId2);
            access.insert(os);
        }
        List<APIKeyOS> actualOSList = access.selectByUserId(applicationId, userId);
        assertEquals(3, actualOSList.size());
        for (APIKeyOS actualOS: actualOSList) {
            for (APIKeyOS os: generatedObjectSegments) {
                if (os.getSecretKey().equals(actualOS.getSecretKey())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    @Test
    public void testSelectByApplicationId() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        for (int i = 0; i < 3; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId);
            access.insert(os);
        }
        String userId2 = getUserId(applicationId);
        for (int i = 0; i < 2; i ++) {
            APIKeyOS os = createObjectSegment(applicationId, userId2);
            access.insert(os);
        }
        PaginatedResult<List<APIKeyOS>> result = access.selectByApplicationId(applicationId);
        List<APIKeyOS> actualOSList = result.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(5, actualOSList.size());
        for (APIKeyOS actualOS: actualOSList) {
            for (APIKeyOS os: generatedObjectSegments) {
                if (os.getSecretKey().equals(actualOS.getSecretKey())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    private void assertOS(APIKeyOS expectedOS, APIKeyOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.getId(), actualOS.getId());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getSecretKey(), actualOS.getSecretKey());
        assertEquals(expectedOS.getName(), actualOS.getName());
        assertEquals(expectedOS.getUserId(), actualOS.getUserId());
        assertEquals(expectedOS.isActive(), actualOS.isActive());
    }

    private APIKeyOS createObjectSegment() {
        String applicationId = getApplicationId();
        String userId = getUserId(applicationId);
        return createObjectSegment(applicationId, userId);
    }

    private APIKeyOS createObjectSegment(String applicationId, String userId) {
        APIKeyOS os = new APIKeyOS(
                applicationId,
                getPrincipalId(applicationId),
                new Date(),
                access.generateSecretKey(),
                generateName(),
                userId,
                true);
        generatedObjectSegments.add(os);
        return os;
    }

    private String generateName() {
        return "api-key-" + new Random().nextInt(10000);
    }

    protected abstract APIKeyAccess getAccess();
    protected abstract String getApplicationId();
    protected abstract String getPrincipalId(String applicationId);
    protected abstract String getUserId(String applicationId);

}
