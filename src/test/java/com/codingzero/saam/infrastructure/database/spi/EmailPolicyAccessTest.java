package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.infrastructure.database.EmailPolicyAccess;
import com.codingzero.saam.infrastructure.database.EmailPolicyOS;
import com.codingzero.utilities.transaction.TransactionManager;
import com.codingzero.utilities.transaction.TransactionManagerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class EmailPolicyAccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private EmailPolicyAccess access;
    private List<EmailPolicyOS> generatedObjectSegments;

    @Before
    public void setUp() {
        generatedObjectSegments = new LinkedList<>();
        access = getAccess();
    }

    @After
    public void clean() {
        for (EmailPolicyOS os: generatedObjectSegments) {
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
        EmailPolicyOS os = createObjectSegment();
        access.insert(os);
        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_DuplicateApplicationId() {
        String applicationId = getApplicationId();
        EmailPolicyOS os = createObjectSegment(applicationId);
        access.insert(os);
        EmailPolicyOS os2 = createObjectSegment(applicationId);
        thrown.expect(Exception.class);
        access.insert(os2);
    }

    @Test
    public void testInsert_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        EmailPolicyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.commit();

        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testInsert_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        EmailPolicyOS os = createObjectSegment();
        manager.start();
        access.insert(os);
        manager.rollback();

        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertNull(actualOS);
    }

    @Test
    public void testUpdate() {
        EmailPolicyOS os = createObjectSegment();
        access.insert(os);
        os.setUpdateTime(new Date());
        os.setDomains(Arrays.asList("foo.com"));
        os.setActive(false);
        os.setMaxLength(100);
        os.setMinLength(9);
        os.setVerificationRequired(false);
        access.update(os);
        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        EmailPolicyOS os = createObjectSegment();
        access.insert(os);
        os.setUpdateTime(new Date());
        os.setDomains(Arrays.asList("foo.com"));
        os.setActive(false);
        os.setMaxLength(100);
        os.setMinLength(9);
        os.setVerificationRequired(false);

        manager.start();
        access.update(os);
        manager.commit();

        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testUpdate_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        EmailPolicyOS os = createObjectSegment();
        access.insert(os);
        EmailPolicyOS os2 = access.selectByApplicationId(os.getApplicationId());
        os.setUpdateTime(new Date());
        os.setDomains(Arrays.asList("foo.com"));
        os.setActive(false);
        os.setMaxLength(100);
        os.setMinLength(9);
        os.setVerificationRequired(false);

        manager.start();
        access.update(os);
        manager.rollback();

        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os2, actualOS);
    }

    @Test
    public void testDelete() {
        EmailPolicyOS os = createObjectSegment();
        access.insert(os);
        access.delete(os);
        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        EmailPolicyOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.commit();

        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertNull(actualOS);
    }

    @Test
    public void testDelete_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        EmailPolicyOS os = createObjectSegment();
        access.insert(os);

        manager.start();
        access.delete(os);
        manager.rollback();

        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testDeleteByApplicationId() {
        String applicationId = getApplicationId();
        EmailPolicyOS os = createObjectSegment(applicationId);
        access.insert(os);
        access.deleteByApplicationId(applicationId);
        EmailPolicyOS actualOS = access.selectByApplicationId(applicationId);
        assertNull(actualOS);
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Commit() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        String applicationId = getApplicationId();
        EmailPolicyOS os = createObjectSegment(applicationId);
        access.insert(os);

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.commit();

        EmailPolicyOS actualOS = access.selectByApplicationId(applicationId);
        assertNull(actualOS);
    }

    @Test
    public void testDeleteByApplicationId_Transaction_Rollback() {
        TransactionManager manager = TransactionManagerBuilder.create().build();
        manager.register("email-policy-access", access);

        String applicationId = getApplicationId();
        EmailPolicyOS os = createObjectSegment(applicationId);
        access.insert(os);

        manager.start();
        access.deleteByApplicationId(applicationId);
        manager.rollback();

        EmailPolicyOS actualOS = access.selectByApplicationId(os.getApplicationId());
        assertOS(os, actualOS);
    }

    @Test
    public void testSelectByIdentifierPolicyOS() {
        EmailPolicyOS os = createObjectSegment();
        access.insert(os);
        EmailPolicyOS actualOS = access.selectByIdentifierPolicyOS(os);
        assertOS(os, actualOS);
    }

    private void assertOS(EmailPolicyOS expectedOS, EmailPolicyOS actualOS) {
        assertEquals(expectedOS.getApplicationId(), actualOS.getApplicationId());
        assertEquals(expectedOS.isVerificationRequired(), actualOS.isVerificationRequired());
        assertEquals(expectedOS.getMinLength(), actualOS.getMinLength());
        assertEquals(expectedOS.getMaxLength(), actualOS.getMaxLength());
        assertEquals(expectedOS.isActive(), actualOS.isActive());
        assertEquals(expectedOS.getCreationTime(), actualOS.getCreationTime());
        assertEquals(expectedOS.getUpdateTime(), actualOS.getUpdateTime());
        assertEquals(expectedOS.getDomains(), actualOS.getDomains());
        assertEquals(expectedOS.getType(), actualOS.getType());
    }

    private EmailPolicyOS createObjectSegment() {
        return createObjectSegment(getApplicationId());
    }

    private EmailPolicyOS createObjectSegment(String applicationId) {
        EmailPolicyOS os = new EmailPolicyOS(
                applicationId,
                true,
                5,
                255,
                true,
                new Date(),
                new Date(),
                Collections.emptyList());
        generatedObjectSegments.add(os);
        return os;
    }

    protected abstract EmailPolicyAccess getAccess();
    protected abstract String getApplicationId();

}
