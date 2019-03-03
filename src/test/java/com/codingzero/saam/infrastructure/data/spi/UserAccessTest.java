package com.codingzero.saam.infrastructure.data.spi;

import com.codingzero.saam.common.PasswordResetCode;
import com.codingzero.saam.common.PrincipalId;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.UserAccess;
import com.codingzero.saam.infrastructure.data.UserOS;
import com.codingzero.utilities.transaction.TransactionManager;
import com.codingzero.utilities.transaction.TransactionManagerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class UserAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserAccess access;
    private List<UserOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (UserOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up apikey, " + os);
            }
        }
    }

    @Test
    public void testInsert() {
        UserOS os = createObjectSegment();
        access.insert(os);

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DifferentApplicationId() {
        String applicationId1 = getApplicationId();
        String id = getPrincipalId(applicationId1, PrincipalType.USER);
        UserOS os = createObjectSegment(applicationId1, id);
        access.insert(os);

        String applicationId2 = getApplicationId();
        UserOS os2 = createObjectSegment(applicationId2, id);
        access.insert(os2);

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("user-access", access);

        UserOS os = createObjectSegment();

        manager.start();
        access.insert(os);
        manager.commit();

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("user-access", access);

        UserOS os = createObjectSegment();

        manager.start();
        access.insert(os);
        manager.rollback();

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertNull(actualOS);
    }

    @Test
    public void testUpdate() {
        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.USER);
        UserOS os = createObjectSegment(applicationId, id);
        access.insert(os);

        os.setPassword("password");
        os.setPasswordResetCode(
                new PasswordResetCode("reset-code", new Date(System.currentTimeMillis() + 1000)));
        os.setRoleIds(Arrays.asList("role-id"));

        access.update(os);

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.USER);
        UserOS os = createObjectSegment(applicationId, id);
        access.insert(os);

        os.setPassword("password");
        os.setPasswordResetCode(
                new PasswordResetCode("reset-code", new Date(System.currentTimeMillis() + 1000)));
        os.setRoleIds(Arrays.asList("role-id"));

        manager.start();
        access.update(os);
        manager.commit();

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.USER);
        UserOS os = createObjectSegment(applicationId, id);
        access.insert(os);

        UserOS oldOS = access.selectByPrincipalOS(os);

        os.setPassword("password");
        os.setPasswordResetCode(
                new PasswordResetCode("reset-code", new Date(System.currentTimeMillis() + 1000)));
        os.setRoleIds(Arrays.asList("role-id"));

        manager.start();
        access.update(os);
        manager.rollback();

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertOS(oldOS, actualOS);
    }

    @Test
    public void testDelete() {
        UserOS os = createObjectSegment();
        access.insert(os);

        access.delete(os);

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("user-access", access);

        UserOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("user-access", access);

        UserOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        UserOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId1 = getApplicationId();
        List<UserOS> osList1 = createObjectSegments(applicationId1, 3);
        for (UserOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<UserOS> osList2 = createObjectSegments(applicationId2, 2);
        for (UserOS os: osList2) {
            access.insert(os);
        }

        access.deleteByApplicationId(applicationId1);

        for (UserOS os: osList1) {
            UserOS actualOS = access.selectByPrincipalOS(os);
            assertNull(actualOS);
        }
        for (UserOS os: osList2) {
            UserOS actualOS = access.selectByPrincipalOS(os);
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId1 = getApplicationId();
        List<UserOS> osList1 = createObjectSegments(applicationId1, 3);
        for (UserOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<UserOS> osList2 = createObjectSegments(applicationId2, 2);
        for (UserOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.commit();

        for (UserOS os: osList1) {
            UserOS actualOS = access.selectByPrincipalOS(os);
            assertNull(actualOS);
        }
        for (UserOS os: osList2) {
            UserOS actualOS = access.selectByPrincipalOS(os);
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId1 = getApplicationId();
        List<UserOS> osList1 = createObjectSegments(applicationId1, 3);
        for (UserOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<UserOS> osList2 = createObjectSegments(applicationId2, 2);
        for (UserOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.rollback();

        for (UserOS os: osList1) {
            UserOS actualOS = access.selectByPrincipalOS(os);
            assertOS(os, actualOS);
        }
        for (UserOS os: osList2) {
            UserOS actualOS = access.selectByPrincipalOS(os);
            assertOS(os, actualOS);
        }
    }

    private void assertOS(UserOS expectedOS, UserOS actualOS) {
        assertEquals(expectedOS.getId().getApplicationId(), actualOS.getId().getApplicationId());
        assertEquals(expectedOS.getId(), actualOS.getId());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getPassword(), actualOS.getPassword());
        assertEquals(expectedOS.getPasswordResetCode(), actualOS.getPasswordResetCode());
        assertEquals(expectedOS.getRoleIds(), actualOS.getRoleIds());
    }

    private List<UserOS> createObjectSegments(String applicationId, int size) {
        List<UserOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String id = getPrincipalId(applicationId, PrincipalType.USER);
            osList.add(createObjectSegment(applicationId, id));
        }
        return osList;
    }

    private UserOS createObjectSegment() {
        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.USER);
        return createObjectSegment(applicationId, id);
    }

    private UserOS createObjectSegment(String applicationId, String principalId) {
        UserOS os = new UserOS(
                new PrincipalId(applicationId, principalId),
                new Date(),
                null,
                null,
                Collections.emptyList());
        generatedObjectSegments.add(os);
        return os;
    }

    private String generateName() {
        return "role-" + new Random().nextInt(10000);
    }

    protected abstract UserAccess getAccess();
    protected abstract String getApplicationId();
    protected abstract String getPrincipalId(String applicationId, PrincipalType type);

}
