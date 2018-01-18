package com.codingzero.saam.core;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.core.application.EmailPolicyFactoryService;
import com.codingzero.saam.core.application.OAuthIdentifierEntity;
import com.codingzero.saam.core.application.OAuthIdentifierFactoryService;
import com.codingzero.saam.core.application.OAuthIdentifierPolicyEntity;
import com.codingzero.saam.core.application.OAuthIdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyOS;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OAuthIdentifierPolicyEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthIdentifierPolicyOS objectSegment;
    private Application application;
    private OAuthIdentifierFactoryService identifierFactory;
    private OAuthIdentifierRepositoryService identifierRepository;
    private OAuthIdentifierPolicyEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(OAuthIdentifierPolicyOS.class);
        application = mock(Application.class);
        identifierFactory = mock(OAuthIdentifierFactoryService.class);
        identifierRepository = mock(OAuthIdentifierRepositoryService.class);
        entity = new OAuthIdentifierPolicyEntity(
                objectSegment,
                application,
                identifierFactory,
                identifierRepository);
    }

    @Test
    public void testSetConfigurations() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put("key1", "value1");
        when(objectSegment.getConfigurations()).thenReturn(new HashMap<>());
        entity.setConfigurations(configurations);
        verify(objectSegment, times(1)).setConfigurations(configurations);
        verify(objectSegment,times(1)).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetConfigurations_SameValue() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put("key1", "value1");
        when(objectSegment.getConfigurations()).thenReturn(configurations);
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
    public void testAddIdentifier() {
        OAuthIdentifierEntity identifier = mock(OAuthIdentifierEntity.class);
        when(objectSegment.getPlatform()).thenReturn(OAuthPlatform.GOOGLE);
        when(identifier.getPolicy()).thenReturn(entity);
        when(identifierFactory.generate(entity, null, null, null)).thenReturn(identifier);
        entity.addIdentifier(null, null, null);
        assertEquals(1, entity.getDirtyIdentifiers().size());
        assertEquals(identifier, entity.getDirtyIdentifiers().get(0));
    }

    @Test
    public void testUpdateIdentifier() {
        OAuthIdentifierEntity identifier = mock(OAuthIdentifierEntity.class);
        when(objectSegment.getPlatform()).thenReturn(OAuthPlatform.GOOGLE);
        when(identifier.getPolicy()).thenReturn(entity);
        entity.updateIdentifier(identifier);
        assertEquals(1, entity.getDirtyIdentifiers().size());
        assertEquals(identifier, entity.getDirtyIdentifiers().get(0));
    }

    @Test
    public void testUpdateIdentifier_MultipleCall() {
        OAuthIdentifierEntity identifier = mock(OAuthIdentifierEntity.class);
        when(objectSegment.getPlatform()).thenReturn(OAuthPlatform.GOOGLE);
        when(identifier.getPolicy()).thenReturn(entity);
        entity.updateIdentifier(identifier);
        entity.updateIdentifier(identifier);
        assertEquals(1, entity.getDirtyIdentifiers().size());
        assertEquals(identifier, entity.getDirtyIdentifiers().get(0));
    }

    @Test
    public void testRemoveIdentifier() {
        OAuthIdentifierEntity identifier = mock(OAuthIdentifierEntity.class);
        when(objectSegment.getPlatform()).thenReturn(OAuthPlatform.GOOGLE);
        when(identifier.getPolicy()).thenReturn(entity);
        entity.removeIdentifier(identifier);
        verify(identifier, times(1)).markAsVoid();
        assertEquals(1, entity.getDirtyIdentifiers().size());
        assertEquals(identifier, entity.getDirtyIdentifiers().get(0));
    }

    @Test
    public void testFetchIdentifierByUserAndId() {
        String content = "oauth-1234567890";
        OAuthIdentifierEntity identifier = mock(OAuthIdentifierEntity.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn("userid");
        when(identifier.getUser()).thenReturn(user);
        when(identifierRepository.findByContent(entity, content)).thenReturn(identifier);
        OAuthIdentifier foundIdentifier = entity.fetchIdentifierByUserAndId(user, content);
        assertEquals(identifier, foundIdentifier);
    }

    @Test
    public void testFetchIdentifierByUserAndId_NullContent() {
        String content = null;
        User user = mock(User.class);
        OAuthIdentifier foundIdentifier = entity.fetchIdentifierByUserAndId(user, content);
        assertEquals(null, foundIdentifier);
    }

    @Test
    public void testFetchIdentifierByUserAndId_NotSameUser() {
        String content = "oauth-1234567890";
        OAuthIdentifierEntity identifier = mock(OAuthIdentifierEntity.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn("userid");
        when(identifier.getUser()).thenReturn(user);
        when(identifierRepository.findByContent(entity, content)).thenReturn(identifier);
        User user2 = mock(User.class);
        when(user2.getId()).thenReturn("userid-2");
        OAuthIdentifier foundIdentifier = entity.fetchIdentifierByUserAndId(user2, content);
        assertEquals(null, foundIdentifier);
    }

}
