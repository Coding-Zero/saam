package com.codingzero.saam.domain;

import com.codingzero.saam.domain.application.IdentifierPolicyEntity;
import com.codingzero.saam.domain.identifier.IdentifierFactoryService;
import com.codingzero.saam.domain.identifier.IdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IdentifierPolicyEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierPolicyOS objectSegment;
    private Application application;
    private IdentifierFactoryService identifierFactory;
    private IdentifierRepositoryService identifierRepository;
    private IdentifierPolicyEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(IdentifierPolicyOS.class);
        application = mock(Application.class);
        identifierFactory = mock(IdentifierFactoryService.class);
        identifierRepository = mock(IdentifierRepositoryService.class);
        entity = new IdentifierPolicyEntityImpl(
                objectSegment,
                application,
                identifierFactory,
                identifierRepository);
    }

    @Test
    public void testSetVerificationRequired() {
        when(objectSegment.isVerificationRequired()).thenReturn(false);
        entity.setVerificationRequired(true);
        verify(objectSegment, times(1)).setVerificationRequired(true);
        verify(objectSegment, times(1)).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetVerificationRequired_SameValue() {
        when(objectSegment.isVerificationRequired()).thenReturn(true);
        entity.setVerificationRequired(true);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testSetMinLength() {
        when(objectSegment.getMinLength()).thenReturn(2);
        entity.setMinLength(1);
        verify(objectSegment, times(1)).setMinLength(1);
        verify(objectSegment, times(1)).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetMinLength_SameValue() {
        when(objectSegment.getMinLength()).thenReturn(1);
        entity.setMinLength(1);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testSetMaxLength() {
        when(objectSegment.getMaxLength()).thenReturn(2);
        entity.setMaxLength(1);
        verify(objectSegment, times(1)).setMaxLength(1);
        verify(objectSegment, times(1)).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetMaxLength_SameValue() {
        when(objectSegment.getMaxLength()).thenReturn(1);
        entity.setMaxLength(1);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testSetActive() {
        when(objectSegment.isActive()).thenReturn(true);
        entity.setActive(false);
        verify(objectSegment, times(1)).setActive(false);
        verify(objectSegment, times(1)).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetActive_SameValue() {
        when(objectSegment.isActive()).thenReturn(false);
        entity.setActive(false);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testCheck() {
        when(objectSegment.getMinLength()).thenReturn(3);
        when(objectSegment.getMaxLength()).thenReturn(5);
        entity.check("abc");
    }

    @Test
    public void testCheck_Length_TooShort() {
        when(objectSegment.getMinLength()).thenReturn(3);
        when(objectSegment.getMaxLength()).thenReturn(5);
        thrown.expect(BusinessError.class);
        entity.check("1");
    }

    @Test
    public void testCheck_Length_TooLong() {
        when(objectSegment.getMinLength()).thenReturn(3);
        when(objectSegment.getMaxLength()).thenReturn(5);
        thrown.expect(BusinessError.class);
        entity.check("abcdefg");
    }

    private class IdentifierPolicyEntityImpl extends IdentifierPolicyEntity {

        public IdentifierPolicyEntityImpl(IdentifierPolicyOS objectSegment, Application application, IdentifierFactoryService identifierFactory, IdentifierRepositoryService identifierRepository) {
            super(objectSegment, application, identifierFactory, identifierRepository);
        }
    }

}
