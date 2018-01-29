package com.codingzero.saam.infrastructure;

import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.saam.infrastructure.database.spi.APIKeyAccess;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
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

public abstract class APIKeyAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private APIKeyAccess access;
    private List<APIKeyOS> generatedAPIKeys;

    @Before
    public void setUp() {
        generatedAPIKeys = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (APIKeyOS os: generatedAPIKeys) {
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
        APIKeyOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    private void assertOS(APIKeyOS expectedOS, APIKeyOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.getId(), actualOS.getId());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getKey(), actualOS.getKey());
        assertEquals(expectedOS.getName(), actualOS.getName());
        assertEquals(expectedOS.getUserId(), actualOS.getUserId());
        assertEquals(expectedOS.isActive(), actualOS.isActive());
    }

    @Test
    public void testInsert_DuplicateKey() {
        APIKeyOS os = createObjectSegment();
        access.insert(os);
        thrown.expect(Exception.class);
        access.insert(os);
    }

    @Test
    public void testInsert_Transaction() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        APIKeyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        APIKeyOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_DuplicateKey() {
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
        APIKeyOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertEquals(null, actualOS);
    }

    @Test
    public void testUpdate() {
        String newName = generateName();
        APIKeyOS os = createObjectSegment();
        access.insert(os);

        os.setName(newName);
        access.update(os);

        APIKeyOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertEquals(newName, actualOS.getName());
    }

    @Test
    public void testUpdate_Transaction() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        String newName = generateName();
        APIKeyOS os = createObjectSegment();

        manager.start();
        access.insert(os);

        os.setName(newName);
        access.update(os);
        manager.commit();

        APIKeyOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertEquals(newName, actualOS.getName());
    }

    @Test
    public void testDelete() {
        APIKeyOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);

        APIKeyOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertEquals(null, actualOS);
    }

    @Test
    public void testDelete_Transaction() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("apikey-access", access);

        APIKeyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        manager.start();
        access.delete(os);
        manager.commit();

        APIKeyOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertEquals(null, actualOS);
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
                access.generateKey(),
                generateName(),
                userId,
                true);
        generatedAPIKeys.add(os);
        return os;
    }

    private String generateName() {
        return "api-key-" + new Random().nextInt(10000);
    }

    protected abstract APIKeyAccess getAccess();
    protected abstract PrincipalAccess getPrincipalAccess();
    protected abstract String getApplicationId();
    protected abstract String getPrincipalId(String applicationId);
    protected abstract String getUserId(String applicationId);

}
