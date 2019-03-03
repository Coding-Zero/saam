package com.codingzero.saam.domain;

import com.codingzero.saam.domain.principal.apikey.APIKeyEntity;
import com.codingzero.saam.domain.principal.apikey.APIKeyFactoryService;
import com.codingzero.saam.domain.principal.user.UserEntity;
import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.infrastructure.data.APIKeyOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class APIKeyEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private APIKeyOS objectSegment;
    private Application application;
    private User user;
    private APIKeyFactoryService factory;
    private UserRepositoryService userRepository;
    private APIKeyEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(APIKeyOS.class);
        application = mock(Application.class);
        user = mock(User.class);
        factory = mock(APIKeyFactoryService.class);
        userRepository = mock(UserRepositoryService.class);
        entity = new APIKeyEntity(
                objectSegment,
                application,
                user,
                factory,
                userRepository);
    }

    @Test
    public void testSetName() {
        String name = "name1";
        when(objectSegment.getName()).thenReturn("name2");
        entity.setName(name);
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetName_SameValue() {
        String name = "name1";
        when(objectSegment.getName()).thenReturn(name);
        entity.setName(name);
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testGetOwner() {
        UserEntity userEntity = mock(UserEntity.class);
        String applicationId = "app";
        String userId = "user";
        when(application.getId()).thenReturn(applicationId);
        when(objectSegment.getUserId()).thenReturn(userId);
        when(userRepository.findById(application, userId)).thenReturn(userEntity);
        entity = new APIKeyEntity(
                objectSegment,
                application,
                null,
                factory,
                userRepository);
        User user = entity.getOwner();
        assertEquals(userEntity, user);
    }

    @Test
    public void testGetUser_NotNull() {
        UserEntity userEntity = mock(UserEntity.class);
        entity = new APIKeyEntity(
                objectSegment,
                application,
                userEntity,
                factory,
                userRepository);
        User user = entity.getOwner();
        assertEquals(userEntity, user);
    }

}
