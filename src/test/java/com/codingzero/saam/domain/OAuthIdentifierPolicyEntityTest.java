package com.codingzero.saam.domain;

import com.codingzero.saam.domain.application.OAuthIdentifierPolicyEntity;
import com.codingzero.saam.domain.oauthidentifier.OAuthIdentifierFactoryService;
import com.codingzero.saam.domain.oauthidentifier.OAuthIdentifierRepositoryService;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.HashMap;
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
                application
        );
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

}
