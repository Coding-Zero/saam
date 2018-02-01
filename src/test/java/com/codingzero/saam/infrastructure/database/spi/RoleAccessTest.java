package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.RoleOS;
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

public abstract class RoleAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private RoleAccess access;
    private List<RoleOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (RoleOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up apikey, " + os);
            }
        }
    }

    @Test
    public void testIsDuplicateName() {
        String applicationId = getApplicationId();
        String name = generateName();

        boolean isDuplicate = access.isDuplicateName(applicationId, name);
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicateName_Duplicate() {
        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.ROLE);
        String name = generateName();
        RoleOS os = createObjectSegment(applicationId, id, name);
        access.insert(os);

        boolean isDuplicate = access.isDuplicateName(applicationId, name);
        assertTrue(isDuplicate);
    }

    @Test
    public void testInsert() {
        RoleOS os = createObjectSegment();
        access.insert(os);

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DifferentApplicationId() {
        String applicationId1 = getApplicationId();
        String id = getPrincipalId(applicationId1, PrincipalType.ROLE);
        String name = generateName();
        RoleOS os = createObjectSegment(applicationId1, id, name);
        access.insert(os);

        String applicationId2 = getApplicationId();
        RoleOS os2 = createObjectSegment(applicationId2, id, name);
        access.insert(os2);

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicateName() {
        String applicationId = getApplicationId();
        String id1 = getPrincipalId(applicationId, PrincipalType.ROLE);
        String name = generateName();
        RoleOS os = createObjectSegment(applicationId, id1, name);
        access.insert(os);

        String id2 = getPrincipalId(applicationId, PrincipalType.ROLE);
        RoleOS os2 = createObjectSegment(applicationId, id2, name);

        thrown.expect(Exception.class);
        access.insert(os2);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        RoleOS os = createObjectSegment();

        manager.start();
        access.insert(os);
        manager.commit();

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        RoleOS os = createObjectSegment();

        manager.start();
        access.insert(os);
        manager.rollback();

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertNull(actualOS);
    }

    @Test
    public void testUpdate() {
        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.ROLE);
        String name = generateName();
        RoleOS os = createObjectSegment(applicationId, id, name);
        access.insert(os);

        String name2 = generateName();
        os.setName(name2);

        access.update(os);

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);

        actualOS = access.selectByName(os.getApplicationId(), name);
        assertNull(actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.ROLE);
        String name = generateName();
        RoleOS os = createObjectSegment(applicationId, id, name);
        access.insert(os);

        String name2 = generateName();
        os.setName(name2);

        manager.start();
        access.update(os);
        manager.commit();

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);

        actualOS = access.selectByName(os.getApplicationId(), name);
        assertNull(actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.ROLE);
        String name = generateName();
        RoleOS os = createObjectSegment(applicationId, id, name);
        access.insert(os);

        RoleOS oldOS = access.selectByName(os.getApplicationId(), name);

        String name2 = generateName();
        os.setName(name2);

        manager.start();
        access.update(os);
        manager.rollback();

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertOS(oldOS, actualOS);

        actualOS = access.selectByName(os.getApplicationId(), name2);
        assertNull(actualOS);
    }

    @Test
    public void testDelete() {
        RoleOS os = createObjectSegment();
        access.insert(os);

        access.delete(os);

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        RoleOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        RoleOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        RoleOS actualOS = access.selectByPrincipalOS(os);
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId1 = getApplicationId();
        List<RoleOS> osList1 = createObjectSegments(applicationId1, 3);
        for (RoleOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<RoleOS> osList2 = createObjectSegments(applicationId2, 2);
        for (RoleOS os: osList2) {
            access.insert(os);
        }

        access.deleteByApplicationId(applicationId1);

        for (RoleOS os: osList1) {
            RoleOS actualOS = access.selectByPrincipalOS(os);
            assertNull(actualOS);
        }
        for (RoleOS os: osList2) {
            RoleOS actualOS = access.selectByPrincipalOS(os);
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId1 = getApplicationId();
        List<RoleOS> osList1 = createObjectSegments(applicationId1, 3);
        for (RoleOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<RoleOS> osList2 = createObjectSegments(applicationId2, 2);
        for (RoleOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.commit();

        for (RoleOS os: osList1) {
            RoleOS actualOS = access.selectByPrincipalOS(os);
            assertNull(actualOS);
        }
        for (RoleOS os: osList2) {
            RoleOS actualOS = access.selectByPrincipalOS(os);
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("role-access", access);

        String applicationId1 = getApplicationId();
        List<RoleOS> osList1 = createObjectSegments(applicationId1, 3);
        for (RoleOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<RoleOS> osList2 = createObjectSegments(applicationId2, 2);
        for (RoleOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.rollback();

        for (RoleOS os: osList1) {
            RoleOS actualOS = access.selectByPrincipalOS(os);
            assertOS(os, actualOS);
        }
        for (RoleOS os: osList2) {
            RoleOS actualOS = access.selectByPrincipalOS(os);
            assertOS(os, actualOS);
        }
    }

    private void assertOS(RoleOS expectedOS, RoleOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.getId(), actualOS.getId());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getName(), actualOS.getName());
    }

    private List<RoleOS> createObjectSegments(String applicationId, int size) {
        List<RoleOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String id = getPrincipalId(applicationId, PrincipalType.ROLE);
            String name = generateName();
            osList.add(createObjectSegment(applicationId, id, name));
        }
        return osList;
    }

    private RoleOS createObjectSegment() {
        String applicationId = getApplicationId();
        String id = getPrincipalId(applicationId, PrincipalType.ROLE);
        String name = generateName();
        return createObjectSegment(applicationId, id, name);
    }

    private RoleOS createObjectSegment(String applicationId, String id, String name) {
        RoleOS os = new RoleOS(
                applicationId,
                id,
                new Date(),
                name);
        generatedObjectSegments.add(os);
        return os;
    }

    private String generateName() {
        return "role-" + new Random().nextInt(10000);
    }

    protected abstract RoleAccess getAccess();
    protected abstract String getApplicationId();
    protected abstract String getPrincipalId(String applicationId, PrincipalType type);

}
