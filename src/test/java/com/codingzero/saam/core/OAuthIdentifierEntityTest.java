package com.codingzero.saam.core;

import com.codingzero.saam.core.application.EmailPolicyEntity;
import com.codingzero.saam.core.application.IdentifierFactoryService;
import com.codingzero.saam.core.application.IdentifierRepositoryService;
import com.codingzero.saam.core.application.OAuthIdentifierEntity;
import com.codingzero.saam.core.application.UserRepositoryService;
import com.codingzero.saam.core.application.UsernamePolicyEntity;
import com.codingzero.saam.core.application.UsernamePolicyFactoryService;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.saam.infrastructure.database.UsernamePolicyOS;
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

public class OAuthIdentifierEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthIdentifierOS objectSegment;
    private OAuthIdentifierPolicy policy;
    private User user;
    private UserRepositoryService userRepository;
    private OAuthIdentifierEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(OAuthIdentifierOS.class);
        policy = mock(OAuthIdentifierPolicy.class);
        user = mock(User.class);
        userRepository = mock(UserRepositoryService.class);
        entity = new OAuthIdentifierEntity(
                objectSegment,
                policy,
                user,
                userRepository);
    }

    @Test
    public void testSetConfigurations() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        when(objectSegment.getProperties()).thenReturn(new HashMap<>());
        entity.setProperties(properties);
        verify(objectSegment, times(1)).setProperties(properties);
        verify(objectSegment,times(1)).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetConfigurations_SameValue() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        when(objectSegment.getProperties()).thenReturn(properties);
        assertEquals(false, entity.isDirty());
    }

}
