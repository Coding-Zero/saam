package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.PasswordPolicy;
import com.codingzero.saam.infrastructure.database.ApplicationOS;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class ApplicationAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ApplicationAccess access;
    private List<ApplicationOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (ApplicationOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                System.err.println("sometime wrong during clean up, " + os);
            }
        }
    }

    @Test
    public void testIsDuplicateName() {
        ApplicationOS os = createObjectSegment();
        access.insert(os);
        boolean isDuplicate = access.isDuplicateName(generateName());
        assertFalse(isDuplicate);
    }

    @Test
    public void testIsDuplicateName_Duplicate() {
        ApplicationOS os = createObjectSegment();
        access.insert(os);
        boolean isDuplicate = access.isDuplicateName(os.getName());
        assertTrue(isDuplicate);
    }

    @Test
    public void testInsert() {
        ApplicationOS os = createObjectSegment();
        access.insert(os);
        ApplicationOS actualOS = access.selectById(os.getId());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicateName() {
        ApplicationOS os = createObjectSegment();
        access.insert(os);
        ApplicationOS os2 = createObjectSegment();
        os2.setName(os.getName());
        thrown.expect(Exception.class);
        access.insert(os2);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("application-access", access);

        ApplicationOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        ApplicationOS actualOS = access.selectById(os.getId());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("application-access", access);

        ApplicationOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.rollback();

        ApplicationOS actualOS = access.selectById(os.getId());
        assertNull(actualOS);
    }

    @Test
    public void testUpdate() {
        ApplicationOS os = createObjectSegment();
        access.insert(os);
        os.setDescription("description");
        os.setName("name-123");
        os.setPasswordPolicy(new PasswordPolicy(3, 10, true, true));
        os.setStatus(ApplicationStatus.DEACTIVE);
        access.update(os);
        ApplicationOS actualOS = access.selectById(os.getId());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("application-access", access);

        ApplicationOS os = createObjectSegment();
        access.insert(os);
        os.setDescription("description");
        os.setName("name-123");
        os.setPasswordPolicy(new PasswordPolicy(3, 10, true, true));
        os.setStatus(ApplicationStatus.DEACTIVE);
        manager.start();
        access.update(os);
        manager.commit();

        ApplicationOS actualOS = access.selectById(os.getId());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("application-access", access);

        ApplicationOS os = createObjectSegment();
        access.insert(os);
        ApplicationOS oldOS = access.selectById(os.getId());
        os.setDescription("description");
        os.setName("name-123");
        os.setPasswordPolicy(new PasswordPolicy(3, 10, true, true));
        os.setStatus(ApplicationStatus.DEACTIVE);
        manager.start();
        access.update(os);
        manager.rollback();

        ApplicationOS actualOS = access.selectById(os.getId());
        assertOS(oldOS, actualOS);
    }

    @Test
    public void testUpdate_DuplicateName() {
        ApplicationOS os = createObjectSegment();
        access.insert(os);
        ApplicationOS os2 = createObjectSegment();
        access.insert(os2);
        os2.setName(os.getName());
        thrown.expect(Exception.class);
        access.update(os2);
    }

    @Test
    public void testDelete() {
        ApplicationOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);
        ApplicationOS actualOS = access.selectById(os.getId());
        assertEquals(null, actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("application-access", access);

        ApplicationOS os = createObjectSegment();
        access.insert(os);
        manager.start();
        access.delete(os);
        manager.commit();
        ApplicationOS actualOS = access.selectById(os.getId());
        assertEquals(null, actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("application-access", access);

        ApplicationOS os = createObjectSegment();
        access.insert(os);
        manager.start();
        access.delete(os);
        manager.rollback();
        ApplicationOS actualOS = access.selectById(os.getId());
        assertOS(os, actualOS);
    }

    @Test
    public void testSelectAll() {
        for (int i = 0; i < 3; i ++) {
            ApplicationOS os = createObjectSegment();
            access.insert(os);
        }
        PaginatedResult<List<ApplicationOS>> result = access.selectAll();
        List<ApplicationOS> osList = result.start(new OffsetBasedResultPage(1, 10)).getResult();
        assertEquals(3, osList.size());
        for (ApplicationOS actualOS: osList) {
            for (ApplicationOS os: generatedObjectSegments) {
                if (os.getId().equals(actualOS.getId())) {
                    assertOS(os, actualOS);
                }
            }
        }
    }

    private void assertOS(ApplicationOS expectedOS, ApplicationOS actualOS) {
        assertEquals(expectedOS.getId(), actualOS.getId());
        assertEquals(expectedOS.getName(), actualOS.getName());
        assertEquals(expectedOS.getDescription(), actualOS.getDescription());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getPasswordPolicy(), actualOS.getPasswordPolicy());
        assertEquals(expectedOS.getStatus(), actualOS.getStatus());
    }

    private ApplicationOS createObjectSegment() {
        String applicationId = access.generateId();
        return createObjectSegment(applicationId);
    }

    private ApplicationOS createObjectSegment(String applicationId) {
        ApplicationOS os = new ApplicationOS(
                applicationId,
                generateName(),
                "",
                new Date(),
                null,
                ApplicationStatus.ACTIVE);
        generatedObjectSegments.add(os);
        return os;
    }

    private String generateName() {
        return "app-name-" + new Random().nextInt(10000);
    }

    protected abstract ApplicationAccess getAccess();

}
