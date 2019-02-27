package com.codingzero.saam.domain;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.domain.oauthidentifier.OAuthIdentifierEntity;
import com.codingzero.saam.domain.principal.user.UserEntity;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
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

public class OAuthIdentifierEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthIdentifierOS objectSegment;
    private OAuthIdentifierPolicy policy;
    private User user;
    private Application application;
    private UserRepositoryService userRepository;
    private OAuthIdentifierEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(OAuthIdentifierOS.class);
        policy = mock(OAuthIdentifierPolicy.class);
        user = mock(User.class);
        application = mock(Application.class);
        userRepository = mock(UserRepositoryService.class);
        entity = new OAuthIdentifierEntity(
                objectSegment,
                application,
                user,
                userRepository);
    }

    @Test
    public void testGetUser() {
        UserEntity userEntity = mock(UserEntity.class);
        String applicationId = "app";
        String userId = "user";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(policy.getApplication()).thenReturn(application);
        when(objectSegment.getUserId()).thenReturn(userId);
        when(userRepository.findById(application, userId)).thenReturn(userEntity);
        entity = new OAuthIdentifierEntity(
                objectSegment,
                application, null,
                userRepository);
        User user = entity.getUser();
        assertEquals(userEntity, user);
    }

    @Test
    public void testGetUser_NotNull() {
        UserEntity userEntity = mock(UserEntity.class);
        entity = new OAuthIdentifierEntity(
                objectSegment,
                application, userEntity,
                userRepository);
        User user = entity.getUser();
        assertEquals(userEntity, user);
    }

    @Test
    public void testGetPolicy() {
        OAuthIdentifierPolicy policy = mock(OAuthIdentifierPolicy.class);
        Application application = mock(Application.class);
        when(objectSegment.getKey().getPlatform()).thenReturn(OAuthPlatform.GOOGLE);
        when(user.getApplication()).thenReturn(application);
        when(application.fetchOAuthIdentifierPolicy(OAuthPlatform.GOOGLE)).thenReturn(policy);
        entity = new OAuthIdentifierEntity(
                objectSegment,
                application, user,
                userRepository);
        OAuthIdentifierPolicy foundPolicy = entity.getPolicy();
        assertEquals(policy, foundPolicy);
    }

    @Test
    public void testGetPolicy_NotNull() {
        entity = new OAuthIdentifierEntity(
                objectSegment,
                application, user,
                userRepository);
        OAuthIdentifierPolicy foundPolicy = entity.getPolicy();
        assertEquals(policy, foundPolicy);
    }

    @Test
    public void testSetProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        when(objectSegment.getProperties()).thenReturn(new HashMap<>());
        entity.setProperties(properties);
        verify(objectSegment, times(1)).setProperties(properties);
        verify(objectSegment,times(1)).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetProperties_SameValue() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        when(objectSegment.getProperties()).thenReturn(properties);
        assertEquals(false, entity.isDirty());
    }

}
