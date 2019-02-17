package com.codingzero.saam.domain;

import com.codingzero.saam.domain.identifier.IdentifierEntity;
import com.codingzero.saam.domain.identifier.IdentifierFactoryService;
import com.codingzero.saam.domain.application.IdentifierPolicyEntity;
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

    @Test
    public void testAddIdentifier() {
        String content = "id1234567890";
        IdentifierEntity identifier = mock(IdentifierEntity.class);
        when(identifier.getContent()).thenReturn(content);
        when(identifierFactory.generate(entity, null, null)).thenReturn(identifier);
        entity.addIdentifier(null, null);
        assertEquals(1, entity.getDirtyIdentifiers().size());
        assertEquals(identifier, entity.getDirtyIdentifiers().get(0));
    }

    @Test
    public void testUpdateIdentifier() {
        String content = "id1234567890";
        IdentifierEntity identifier = mock(IdentifierEntity.class);
        when(identifier.getContent()).thenReturn(content);
        entity.updateIdentifier(identifier);
        assertEquals(1, entity.getDirtyIdentifiers().size());
        assertEquals(identifier, entity.getDirtyIdentifiers().get(0));
    }

    @Test
    public void testUpdateIdentifier_MultipleCall() {
        String content = "id1234567890";
        IdentifierEntity identifier = mock(IdentifierEntity.class);
        when(identifier.getContent()).thenReturn(content);
        entity.updateIdentifier(identifier);
        entity.updateIdentifier(identifier);
        assertEquals(1, entity.getDirtyIdentifiers().size());
        assertEquals(identifier, entity.getDirtyIdentifiers().get(0));
    }

    @Test
    public void testRemoveIdentifier() {
        String content = "id1234567890";
        IdentifierEntity identifier = mock(IdentifierEntity.class);
        when(identifier.getContent()).thenReturn(content);
        entity.removeIdentifier(identifier);
        verify(identifier, times(1)).markAsVoid();
        assertEquals(1, entity.getDirtyIdentifiers().size());
        assertEquals(identifier, entity.getDirtyIdentifiers().get(0));
    }

    @Test
    public void testFetchIdentifierByUserAndId() {
        String content = "id1234567890";
        IdentifierEntity identifier = mock(IdentifierEntity.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn("userid");
        when(identifier.getUser()).thenReturn(user);
        when(identifierRepository.findByContent(entity, content)).thenReturn(identifier);
        Identifier foundIdentifier = entity.fetchIdentifierByUserAndContent(user, content);
        assertEquals(identifier, foundIdentifier);
    }

    @Test
    public void testFetchIdentifierByUserAndId_NullContent() {
        String content = null;
        User user = mock(User.class);
        Identifier foundIdentifier = entity.fetchIdentifierByUserAndContent(user, content);
        assertEquals(null, foundIdentifier);
    }

    @Test
    public void testFetchIdentifierByUserAndId_NotSameUser() {
        String content = "id1234567890";
        IdentifierEntity identifier = mock(IdentifierEntity.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn("userid");
        when(identifier.getUser()).thenReturn(user);
        when(identifierRepository.findByContent(entity, content)).thenReturn(identifier);
        User user2 = mock(User.class);
        when(user2.getId()).thenReturn("userid-2");
        Identifier foundIdentifier = entity.fetchIdentifierByUserAndContent(user2, content);
        assertEquals(null, foundIdentifier);
    }

    private class IdentifierPolicyEntityImpl extends IdentifierPolicyEntity {

        public IdentifierPolicyEntityImpl(IdentifierPolicyOS objectSegment, Application application, IdentifierFactoryService identifierFactory, IdentifierRepositoryService identifierRepository) {
            super(objectSegment, application, identifierFactory, identifierRepository);
        }
    }

}
