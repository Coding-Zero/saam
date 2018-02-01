package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.PermissionOS;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class PermissionAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PermissionAccess access;
    private List<PermissionOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (PermissionOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("sometime wrong during clean up, " + os);
            }
        }
    }

    @Test
    public void testIsDuplicate() {
        String applicationId = getApplicationId();
        String resourceKey = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        boolean isDuplicate = access.isDuplicate(applicationId, resourceKey, principalId);
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicate_Duplicate() {
        String applicationId = getApplicationId();
        String resourceKey = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);

        PermissionOS os = createObjectSegment(applicationId, resourceKey, principalId);
        access.insert(os);

        boolean isDuplicate = access.isDuplicate(applicationId, resourceKey, principalId);
        assertTrue(isDuplicate);
    }

    @Test
    public void testInsert() {
        PermissionOS os = createObjectSegment();
        access.insert(os);
        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Duplicate() {
        PermissionOS os = createObjectSegment();
        access.insert(os);
        thrown.expect(Exception.class);
        access.insert(os);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        PermissionOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        PermissionOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.rollback();

        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertNull(actualOS);
    }

    @Test
    public void testUpdate() {
        PermissionOS os = createObjectSegment();
        access.insert(os);

        os.setActions(Arrays.asList(new Action("READ", true)));

        access.update(os);

        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        PermissionOS os = createObjectSegment();
        access.insert(os);

        os.setActions(Arrays.asList(new Action("READ", true)));

        manager.start();
        access.update(os);
        manager.commit();

        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        PermissionOS os = createObjectSegment();
        access.insert(os);

        PermissionOS oldOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());

        os.setActions(Arrays.asList(new Action("READ", true)));

        manager.start();
        access.update(os);
        manager.rollback();

        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertOS(oldOS, actualOS);
    }

    @Test
    public void testDelete() {
        PermissionOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);
        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        PermissionOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        PermissionOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
        assertOS(actualOS, actualOS);
    }

    @Test
    public void testDeleteByPrincipalId() {
        String applicationId = getApplicationId();
        String userId = getPrincipalId(applicationId, PrincipalType.USER);
        List<PermissionOS> users = createObjectSegmentsWithPrincipalId(applicationId, userId, 3);
        for (PermissionOS os: users) {
            access.insert(os);
        }
        String roleId = getPrincipalId(applicationId, PrincipalType.ROLE);
        List<PermissionOS> roles = createObjectSegmentsWithPrincipalId(applicationId, roleId, 2);
        for (PermissionOS os: roles) {
            access.insert(os);
        }

        access.deleteByPrincipalId(applicationId, userId);

        for (PermissionOS os: users) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertNull(actualOS);
        }
        for (PermissionOS os: roles) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByPrincipalId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        String applicationId = getApplicationId();
        String userId = getPrincipalId(applicationId, PrincipalType.USER);
        List<PermissionOS> users = createObjectSegmentsWithPrincipalId(applicationId, userId, 3);
        for (PermissionOS os: users) {
            access.insert(os);
        }
        String roleId = getPrincipalId(applicationId, PrincipalType.ROLE);
        List<PermissionOS> roles = createObjectSegmentsWithPrincipalId(applicationId, roleId, 2);
        for (PermissionOS os: roles) {
            access.insert(os);
        }

        manager.start();
        access.deleteByPrincipalId(applicationId, userId);
        manager.commit();

        for (PermissionOS os: users) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertNull(actualOS);
        }
        for (PermissionOS os: roles) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByPrincipalId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        String applicationId = getApplicationId();
        String userId = getPrincipalId(applicationId, PrincipalType.USER);
        List<PermissionOS> users = createObjectSegmentsWithPrincipalId(applicationId, userId, 3);
        for (PermissionOS os: users) {
            access.insert(os);
        }
        String roleId = getPrincipalId(applicationId, PrincipalType.ROLE);
        List<PermissionOS> roles = createObjectSegmentsWithPrincipalId(applicationId, roleId, 2);
        for (PermissionOS os: roles) {
            access.insert(os);
        }

        manager.start();
        access.deleteByPrincipalId(applicationId, userId);
        manager.rollback();

        for (PermissionOS os: users) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
        for (PermissionOS os: roles) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByResourceKey() {
        String applicationId = getApplicationId();
        String resourceKey1 = getResourceKey();
        List<PermissionOS> osList1 = createObjectSegmentsWithResourceKey(applicationId, resourceKey1, 3);
        for (PermissionOS os: osList1) {
            access.insert(os);
        }
        String resourceKey2 = getResourceKey();
        List<PermissionOS> osList2 = createObjectSegmentsWithResourceKey(applicationId, resourceKey2, 2);
        for (PermissionOS os: osList2) {
            access.insert(os);
        }

        access.deleteByResourceKey(applicationId, resourceKey1);

        for (PermissionOS os: osList1) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertNull(actualOS);
        }
        for (PermissionOS os: osList2) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByResourceKey_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        String applicationId = getApplicationId();
        String resourceKey1 = getResourceKey();
        List<PermissionOS> osList1 = createObjectSegmentsWithResourceKey(applicationId, resourceKey1, 3);
        for (PermissionOS os: osList1) {
            access.insert(os);
        }
        String resourceKey2 = getResourceKey();
        List<PermissionOS> osList2 = createObjectSegmentsWithResourceKey(applicationId, resourceKey2, 2);
        for (PermissionOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByResourceKey(applicationId, resourceKey1);
        manager.commit();

        for (PermissionOS os: osList1) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertNull(actualOS);
        }
        for (PermissionOS os: osList2) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByResourceKey_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        String applicationId = getApplicationId();
        String resourceKey1 = getResourceKey();
        List<PermissionOS> osList1 = createObjectSegmentsWithResourceKey(applicationId, resourceKey1, 3);
        for (PermissionOS os: osList1) {
            access.insert(os);
        }
        String resourceKey2 = getResourceKey();
        List<PermissionOS> osList2 = createObjectSegmentsWithResourceKey(applicationId, resourceKey2, 2);
        for (PermissionOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByResourceKey(applicationId, resourceKey1);
        manager.rollback();

        for (PermissionOS os: osList1) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
        for (PermissionOS os: osList2) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId1 = getApplicationId();
        List<PermissionOS> osList1 = createObjectSegments(applicationId1, 3);
        for (PermissionOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<PermissionOS> osList2 = createObjectSegments(applicationId2, 2);
        for (PermissionOS os: osList2) {
            access.insert(os);
        }

        access.deleteByApplicationId(applicationId1);

        for (PermissionOS os: osList1) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertNull(actualOS);
        }
        for (PermissionOS os: osList2) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        String applicationId1 = getApplicationId();
        List<PermissionOS> osList1 = createObjectSegments(applicationId1, 3);
        for (PermissionOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<PermissionOS> osList2 = createObjectSegments(applicationId2, 2);
        for (PermissionOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.commit();

        for (PermissionOS os: osList1) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertNull(actualOS);
        }
        for (PermissionOS os: osList2) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("permission-access", access);

        String applicationId1 = getApplicationId();
        List<PermissionOS> osList1 = createObjectSegments(applicationId1, 3);
        for (PermissionOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        List<PermissionOS> osList2 = createObjectSegments(applicationId2, 2);
        for (PermissionOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.rollback();

        for (PermissionOS os: osList1) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
        for (PermissionOS os: osList2) {
            PermissionOS actualOS = access.selectByResourceKeyAndPrincipalId(
                    os.getApplicationId(), os.getResourceKey(), os.getPrincipalId());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testSelectByResourceKey() {
        String applicationId = getApplicationId();
        String resourceKey1 = getResourceKey();
        List<PermissionOS> osList1 = createObjectSegmentsWithResourceKey(applicationId, resourceKey1, 3);
        for (PermissionOS os: osList1) {
            access.insert(os);
        }
        String resourceKey2 = getResourceKey();
        List<PermissionOS> osList2 = createObjectSegmentsWithResourceKey(applicationId, resourceKey2, 2);
        for (PermissionOS os: osList2) {
            access.insert(os);
        }

        PaginatedResult<List<PermissionOS>> actualOSListResult =
                access.selectByResourceKey(applicationId, resourceKey1);
        List<PermissionOS> actualOSList = actualOSListResult.start(new OffsetBasedResultPage(1, 10)).getResult();

        assertEquals(3, actualOSList.size());
        for (PermissionOS os: osList1) {
            for (PermissionOS actualOS: actualOSList) {
                if (os.getResourceKey().equalsIgnoreCase(actualOS.getResourceKey())
                        && os.getPrincipalId().equalsIgnoreCase(actualOS.getPrincipalId())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    @Test
    public void testSelectByPrincipalId() {
        String applicationId = getApplicationId();
        String userId = getPrincipalId(applicationId, PrincipalType.USER);
        List<PermissionOS> users = createObjectSegmentsWithPrincipalId(applicationId, userId, 3);
        for (PermissionOS os: users) {
            access.insert(os);
        }
        String roleId = getPrincipalId(applicationId, PrincipalType.ROLE);
        List<PermissionOS> roles = createObjectSegmentsWithResourceKey(applicationId, roleId, 2);
        for (PermissionOS os: roles) {
            access.insert(os);
        }

        PaginatedResult<List<PermissionOS>> actualOSListResult =
                access.selectByPrincipalId(applicationId, userId);
        List<PermissionOS> actualOSList = actualOSListResult.start(new OffsetBasedResultPage(1, 10)).getResult();

        assertEquals(3, actualOSList.size());
        for (PermissionOS os: users) {
            for (PermissionOS actualOS: actualOSList) {
                if (os.getResourceKey().equalsIgnoreCase(actualOS.getResourceKey())
                        && os.getPrincipalId().equalsIgnoreCase(actualOS.getPrincipalId())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    private String getResourceKey() {
        return "resource-" + new Random().nextInt(99999);
    }

    private List<PermissionOS> createObjectSegments(String applicationId, int size) {
        List<PermissionOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String resourceKey = getResourceKey();
            String principalId = getPrincipalId(applicationId, PrincipalType.USER);
            osList.add(createObjectSegment(applicationId, resourceKey, principalId));
        }
        return osList;
    }

    private List<PermissionOS> createObjectSegmentsWithPrincipalId(String applicationId, String principalId, int size) {
        List<PermissionOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String resourceKey = getResourceKey();
            osList.add(createObjectSegment(applicationId, resourceKey, principalId));
        }
        return osList;
    }

    private List<PermissionOS> createObjectSegmentsWithResourceKey(String applicationId, String resourceKey, int size) {
        List<PermissionOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String principalId = getPrincipalId(applicationId, PrincipalType.USER);
            osList.add(createObjectSegment(applicationId, resourceKey, principalId));
        }
        return osList;
    }

    private PermissionOS createObjectSegment() {
        String applicationId = getApplicationId();
        String resourceKey = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        return createObjectSegment(applicationId, resourceKey, principalId);
    }

    private PermissionOS createObjectSegment(String applicationId, String resourceKey, String principalId) {
        PermissionOS os = new PermissionOS(
                applicationId,
                resourceKey,
                principalId,
                new Date(),
                Collections.emptyList());
        generatedObjectSegments.add(os);
        return os;
    }

    private void assertOS(PermissionOS expectedOS, PermissionOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.getResourceKey(), actualOS.getResourceKey());
        assertEquals(expectedOS.getPrincipalId(), actualOS.getPrincipalId());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getActions(), actualOS.getActions());
    }

    protected abstract PermissionAccess getAccess();
    protected abstract String getApplicationId();
    protected abstract String getPrincipalId(String applicationId, PrincipalType type);

}
