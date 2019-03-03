package com.codingzero.saam.infrastructure.data.spi;

import com.codingzero.saam.common.UsernameFormat;
import com.codingzero.saam.infrastructure.data.UsernamePolicyAccess;
import com.codingzero.saam.infrastructure.data.UsernamePolicyOS;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class UsernamePolicyAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UsernamePolicyAccess access;
    private List<UsernamePolicyOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (UsernamePolicyOS os: generatedObjectSegments) {
            try {
                access.delete(os);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("sometime wrong during clean up, " + os);
            }
        }
    }

    @Test
    public void testInsert() {
        UsernamePolicyOS os = createObjectSegment();
        access.insert(os);
        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicateApplicationId() {
        String applicationId = getApplicationId();
        UsernamePolicyOS os = createObjectSegment(applicationId);
        access.insert(os);
        UsernamePolicyOS os2 = createObjectSegment(applicationId);
        thrown.expect(Exception.class);
        access.insert(os2);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        UsernamePolicyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        UsernamePolicyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.rollback();

        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertNull(actualOS);
    }

    @Test
    public void testUpdate() {
        UsernamePolicyOS os = createObjectSegment();
        access.insert(os);
        os.setUpdateTime(new Date());
        os.setActive(false);
        os.setMaxLength(100);
        os.setMinLength(9);
        os.setVerificationRequired(false);
        access.update(os);
        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        UsernamePolicyOS os = createObjectSegment();
        access.insert(os);
        os.setUpdateTime(new Date());
        os.setActive(false);
        os.setMaxLength(100);
        os.setMinLength(9);
        os.setVerificationRequired(false);

        manager.start();
        access.update(os);
        manager.commit();

        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        UsernamePolicyOS os = createObjectSegment();
        access.insert(os);
        UsernamePolicyOS os2 = access.selectByApplicationId(os.getApplicationId());
        os.setUpdateTime(new Date());
        os.setActive(false);
        os.setMaxLength(100);
        os.setMinLength(9);
        os.setVerificationRequired(false);

        manager.start();
        access.update(os);
        manager.rollback();

        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os2, actualOS);
    }

    @Test
    public void testDelete() {
        UsernamePolicyOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);
        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        UsernamePolicyOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        UsernamePolicyOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId = getApplicationId();
        UsernamePolicyOS os = createObjectSegment(applicationId);
        access.insert(os);
        access.deleteByApplicationId(applicationId);
        UsernamePolicyOS actualOS = access.selectByApplicationId(applicationId);
        assertNull(actualOS);
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        String applicationId = getApplicationId();
        UsernamePolicyOS os = createObjectSegment(applicationId);
        access.insert(os);

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.commit();

        UsernamePolicyOS actualOS = access.selectByApplicationId(applicationId);
        assertNull(actualOS);
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        String applicationId = getApplicationId();
        UsernamePolicyOS os = createObjectSegment(applicationId);
        access.insert(os);

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.rollback();

        UsernamePolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testSelectByIdentifierPolicyOS() {
        UsernamePolicyOS os = createObjectSegment();
        access.insert(os);
        UsernamePolicyOS actualOS = access.selectByIdentifierPolicyOS(os);
        assertOS(os, actualOS);
    }

    private void assertOS(UsernamePolicyOS expectedOS, UsernamePolicyOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.isVerificationRequired(), actualOS.isVerificationRequired());
        assertEquals(expectedOS.getMinLength(), actualOS.getMinLength());
        assertEquals(expectedOS.getMaxLength(), actualOS.getMaxLength());
        assertEquals(expectedOS.isActive(), actualOS.isActive());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getUpdateTime(), actualOS.getUpdateTime());
        assertEquals(expectedOS.getType(), actualOS.getType());
    }

    private UsernamePolicyOS createObjectSegment() {
        return createObjectSegment(getApplicationId());
    }

    private UsernamePolicyOS createObjectSegment(String applicationId) {
        UsernamePolicyOS os = new UsernamePolicyOS(
                applicationId,
                5,
                255,
                true,
                new Date(),
                new Date(),
                UsernameFormat.URL_SAFE);
        generatedObjectSegments.add(os);
        return os;
    }

    protected abstract UsernamePolicyAccess getAccess();
    protected abstract String getApplicationId();

}
