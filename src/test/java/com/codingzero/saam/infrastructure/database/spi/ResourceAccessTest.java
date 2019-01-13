package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.common.ResourceKeySeparator;
import com.codingzero.saam.infrastructure.database.ResourceAccess;
import com.codingzero.saam.infrastructure.database.ResourceOS;
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

public abstract class ResourceAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ResourceAccess access;
    private List<ResourceOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (ResourceOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up apikey, " + os);
            }
        }
    }

    @Test
    public void testIsDuplicateKey() {
        String applicationId = getApplicationId();
        String key = getResourceKey();

        boolean isDuplicate = access.isDuplicateKey(applicationId, key);
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicateKey_DifferentApplicationId() {
        String applicationId = getApplicationId();
        String key = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        ResourceOS os = createObjectSegment(applicationId, principalId, key);
        access.insert(os);

        String applicationId2 = getApplicationId();
        boolean isDuplicate = access.isDuplicateKey(applicationId2, key);
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicateKey_ParentKey() {
        String applicationId = getApplicationId();
        String key = getResourceKey();
        key = getResourceKey(key);
        key = getResourceKey(key);

        boolean isDuplicate = access.isDuplicateKey(applicationId, key);
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicateKey_Duplicate() {
        ResourceOS os = createObjectSegment();
        access.insert(os);

        boolean isDuplicate = access.isDuplicateKey(os.getApplicationId(), os.getKey());
        assertTrue(isDuplicate);
    }

    @Test
    public void testIsDuplicateKey_ParentKey_Duplicate() {
        String applicationId = getApplicationId();
        String key = getResourceKey();
        key = getResourceKey(key);
        key = getResourceKey(key);
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        ResourceOS os = createObjectSegment(applicationId, principalId, key);
        access.insert(os);

        boolean isDuplicate = access.isDuplicateKey(applicationId, key);
        assertTrue(isDuplicate);
    }

    @Test
    public void testInsert() {
        ResourceOS os = createObjectSegment();
        access.insert(os);

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DifferentApplicationId() {
        String applicationId = getApplicationId();
        String key = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        ResourceOS os = createObjectSegment(applicationId, principalId, key);
        access.insert(os);

        String applicationId2 = getApplicationId();
        String principalId2 = getPrincipalId(applicationId2, PrincipalType.ROLE);
        ResourceOS os2 = createObjectSegment(applicationId2, principalId2, key);
        access.insert(os2);

        ResourceOS actualOS = access.selectByKey(applicationId, key);
        assertOS(os, actualOS);
        ResourceOS actualOS2 = access.selectByKey(applicationId2, key);
        assertOS(os2, actualOS2);
    }

    @Test
    public void testInsert_DuplicateKey() {
        String applicationId = getApplicationId();
        String key = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        ResourceOS os = createObjectSegment(applicationId, principalId, key);
        access.insert(os);

        String principalId2 = getPrincipalId(applicationId, PrincipalType.USER);
        ResourceOS os2 = createObjectSegment(applicationId, principalId2, key);

        thrown.expect(Exception.class);
        access.insert(os2);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("resource-access", access);

        ResourceOS os = createObjectSegment();

        manager.start();
        access.insert(os);
        manager.commit();

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("resource-access", access);

        ResourceOS os = createObjectSegment();

        manager.start();
        access.insert(os);
        manager.rollback();

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testUpdate() {
        String applicationId = getApplicationId();
        String key = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        ResourceOS os = createObjectSegment(applicationId, principalId, key);
        access.insert(os);

        String roleId = getPrincipalId(applicationId, PrincipalType.ROLE);
        os.setPrincipalId(roleId);

        access.update(os);

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("resource-access", access);

        String applicationId = getApplicationId();
        String key = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        ResourceOS os = createObjectSegment(applicationId, principalId, key);
        access.insert(os);

        String roleId = getPrincipalId(applicationId, PrincipalType.ROLE);
        os.setPrincipalId(roleId);

        manager.start();
        access.update(os);
        manager.commit();

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("resource-access", access);

        String applicationId = getApplicationId();
        String key = getResourceKey();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        ResourceOS os = createObjectSegment(applicationId, principalId, key);
        access.insert(os);

        String roleId = getPrincipalId(applicationId, PrincipalType.ROLE);
        os.setPrincipalId(roleId);

        ResourceOS oldOS = access.selectByKey(os.getApplicationId(), os.getKey());

        manager.start();
        access.update(os);
        manager.rollback();

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(oldOS, actualOS);
    }

    @Test
    public void testDelete() {
        ResourceOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("resource-access", access);

        ResourceOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("resource-access", access);

        ResourceOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId1 = getApplicationId();
        String principalId1 = getPrincipalId(applicationId1, PrincipalType.USER);
        List<ResourceOS> osList1 = createObjectSegmentsWithPrincipalId(applicationId1, principalId1, 3);
        for (ResourceOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        String principalId12 = getPrincipalId(applicationId2, PrincipalType.USER);
        List<ResourceOS> osList2 = createObjectSegmentsWithPrincipalId(applicationId2, principalId12, 2);
        for (ResourceOS os: osList2) {
            access.insert(os);
        }

        access.deleteByApplicationId(applicationId1);

        for (ResourceOS os: osList1) {
            ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertNull(actualOS);
        }

        for (ResourceOS os: osList2) {
            ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("resource-access", access);

        String applicationId1 = getApplicationId();
        String principalId1 = getPrincipalId(applicationId1, PrincipalType.USER);
        List<ResourceOS> osList1 = createObjectSegmentsWithPrincipalId(applicationId1, principalId1, 3);
        for (ResourceOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        String principalId12 = getPrincipalId(applicationId2, PrincipalType.USER);
        List<ResourceOS> osList2 = createObjectSegmentsWithPrincipalId(applicationId2, principalId12, 2);
        for (ResourceOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.commit();

        for (ResourceOS os: osList1) {
            ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertNull(actualOS);
        }

        for (ResourceOS os: osList2) {
            ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("resource-access", access);

        String applicationId1 = getApplicationId();
        String principalId1 = getPrincipalId(applicationId1, PrincipalType.USER);
        List<ResourceOS> osList1 = createObjectSegmentsWithPrincipalId(applicationId1, principalId1, 3);
        for (ResourceOS os: osList1) {
            access.insert(os);
        }
        String applicationId2 = getApplicationId();
        String principalId12 = getPrincipalId(applicationId2, PrincipalType.USER);
        List<ResourceOS> osList2 = createObjectSegmentsWithPrincipalId(applicationId2, principalId12, 2);
        for (ResourceOS os: osList2) {
            access.insert(os);
        }

        manager.start();
        access.deleteByApplicationId(applicationId1);
        manager.rollback();

        for (ResourceOS os: osList1) {
            ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }

        for (ResourceOS os: osList2) {
            ResourceOS actualOS = access.selectByKey(os.getApplicationId(), os.getKey());
            assertOS(os, actualOS);
        }
    }

    @Test
    public void testSelectByPrincipalId() {
        String applicationId = getApplicationId();
        String userId = getPrincipalId(applicationId, PrincipalType.USER);
        List<ResourceOS> osList1 = createObjectSegmentsWithPrincipalId(applicationId, userId, 3);
        for (ResourceOS os: osList1) {
            access.insert(os);
        }
        String roleIds = getPrincipalId(applicationId, PrincipalType.ROLE);
        List<ResourceOS> osList2 = createObjectSegmentsWithPrincipalId(applicationId, roleIds, 2);
        for (ResourceOS os: osList2) {
            access.insert(os);
        }

        PaginatedResult<List<ResourceOS>> actualOSListResult =
                access.selectByPrincipalId(applicationId, null, userId);
        List<ResourceOS> actualOSList =
                actualOSListResult.start(new OffsetBasedResultPage(1, 10)).getResult();

        assertEquals(3, actualOSList.size());
        for (ResourceOS os: osList1) {
            for (ResourceOS actualOS: actualOSList) {
                if (os.getPrincipalId().equalsIgnoreCase(actualOS.getPrincipalId())
                        && os.getKey().equalsIgnoreCase(actualOS.getKey())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    @Test
    public void testSelectByPrincipalId_ParentResourceKey() {
        String applicationId = getApplicationId();
        String parentKey = getResourceKey();
        parentKey = getResourceKey(parentKey);
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        List<ResourceOS> osList1 = createObjectSegments(applicationId, principalId, parentKey, 3);
        for (ResourceOS os: osList1) {
            access.insert(os);
        }
        List<ResourceOS> osList2 = createObjectSegmentsWithPrincipalId(applicationId, principalId, 2);
        for (ResourceOS os: osList2) {
            access.insert(os);
        }

        PaginatedResult<List<ResourceOS>> actualOSListResult =
                access.selectByPrincipalId(applicationId, parentKey, principalId);
        List<ResourceOS> actualOSList =
                actualOSListResult.start(new OffsetBasedResultPage(1, 10)).getResult();

        assertEquals(3, actualOSList.size());
        for (ResourceOS os: osList1) {
            for (ResourceOS actualOS: actualOSList) {
                if (os.getPrincipalId().equalsIgnoreCase(actualOS.getPrincipalId())
                        && os.getKey().equalsIgnoreCase(actualOS.getKey())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    @Test
    public void testSelectAll() {
        String applicationId = getApplicationId();
        String userId = getPrincipalId(applicationId, PrincipalType.USER);
        List<ResourceOS> osList1 = createObjectSegmentsWithPrincipalId(applicationId, userId, 3);
        for (ResourceOS os: osList1) {
            access.insert(os);
        }
        String roleIds = getPrincipalId(applicationId, PrincipalType.ROLE);
        List<ResourceOS> osList2 = createObjectSegmentsWithPrincipalId(applicationId, roleIds, 2);
        for (ResourceOS os: osList2) {
            access.insert(os);
        }

        PaginatedResult<List<ResourceOS>> actualOSListResult = access.selectAll(applicationId, null);
        List<ResourceOS> actualOSList =
                actualOSListResult.start(new OffsetBasedResultPage(1, 10)).getResult();

        assertEquals(5, actualOSList.size());
        for (ResourceOS os: osList1) {
            for (ResourceOS actualOS: actualOSList) {
                if (os.getPrincipalId().equalsIgnoreCase(actualOS.getPrincipalId())
                        && os.getKey().equalsIgnoreCase(actualOS.getKey())) {
                    assertOS(os, actualOS);
                }
            }
        }
        for (ResourceOS os: osList2) {
            for (ResourceOS actualOS: actualOSList) {
                if (os.getPrincipalId().equalsIgnoreCase(actualOS.getPrincipalId())
                        && os.getKey().equalsIgnoreCase(actualOS.getKey())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    @Test
    public void testSelectAll_ParentResourceKey() {
        String applicationId = getApplicationId();
        String parentKey = getResourceKey();
        List<ResourceOS> osList1 = createObjectSegmentsWithParentKey(applicationId, parentKey, 3);
        for (ResourceOS os: osList1) {
            access.insert(os);
        }
        List<ResourceOS> osList2 = createObjectSegmentsWithParentKey(applicationId, null, 2);
        for (ResourceOS os: osList2) {
            access.insert(os);
        }

        PaginatedResult<List<ResourceOS>> actualOSListResult = access.selectAll(applicationId, parentKey);
        List<ResourceOS> actualOSList =
                actualOSListResult.start(new OffsetBasedResultPage(1, 10)).getResult();

        assertEquals(3, actualOSList.size());
        for (ResourceOS os: osList1) {
            for (ResourceOS actualOS: actualOSList) {
                if (os.getPrincipalId().equalsIgnoreCase(actualOS.getPrincipalId())
                        && os.getKey().equalsIgnoreCase(actualOS.getKey())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    private void assertOS(ResourceOS expectedOS, ResourceOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.getParentKey(), actualOS.getParentKey());
        assertEquals(expectedOS.getKey(), actualOS.getKey());
        assertEquals(expectedOS.getPrincipalId(), actualOS.getPrincipalId());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
    }

    private List<ResourceOS> createObjectSegmentsWithPrincipalId(String applicationId, String principalId, int size) {
        List<ResourceOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String key = getResourceKey();
            osList.add(createObjectSegment(applicationId, principalId, key));
        }
        return osList;
    }

    private List<ResourceOS> createObjectSegmentsWithParentKey(String applicationId, String parentKey, int size) {
        List<ResourceOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String key = getResourceKey(parentKey);
            String principalId = getPrincipalId(applicationId, PrincipalType.USER);
            osList.add(createObjectSegment(applicationId, principalId, key));
        }
        return osList;
    }

    private List<ResourceOS> createObjectSegments(String applicationId, String principalId, String parentKey, int size) {
        List<ResourceOS> osList = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            String key = getResourceKey(parentKey);
            osList.add(createObjectSegment(applicationId, principalId, key));
        }
        return osList;
    }


    private ResourceOS createObjectSegment() {
        String applicationId = getApplicationId();
        String principalId = getPrincipalId(applicationId, PrincipalType.USER);
        String key = getResourceKey();
        return createObjectSegment(applicationId, principalId, key);
    }

    private ResourceOS createObjectSegment(String applicationId, String principalId, String key) {
        ResourceOS os = new ResourceOS(
                applicationId,
                key,
                principalId,
                new Date());
        generatedObjectSegments.add(os);
        return os;
    }

    private String getResourceKey(String parentKey) {
        String key = "resource-" + new Random().nextInt(99999);;
        if (null == parentKey) {
            return key;
        }
        return parentKey + ResourceKeySeparator.VALUE + key;
    }

    private String getResourceKey() {
        return getResourceKey(null);
    }

    protected abstract ResourceAccess getAccess();
    protected abstract String getApplicationId();
    protected abstract String getPrincipalId(String applicationId, PrincipalType type);

}
