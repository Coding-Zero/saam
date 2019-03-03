package com.codingzero.saam.domain;

import com.codingzero.saam.domain.principal.user.UserRepositoryService;
import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.domain.usersession.UserSessionEntity;
import com.codingzero.saam.domain.usersession.UserSessionFactoryService;
import com.codingzero.saam.infrastructure.data.UserSessionAccess;
import com.codingzero.saam.infrastructure.data.UserSessionOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserSessionFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserSessionAccess access;
    private UserRepositoryService userRepository;
    private ApplicationStatusVerifier applicationStatusVerifier;
    private UserSessionFactoryService service;

    @Before
    public void setUp() {
        access = mock(UserSessionAccess.class);
        userRepository = mock(UserRepositoryService.class);
        applicationStatusVerifier = mock(ApplicationStatusVerifier.class);
        service = new UserSessionFactoryService(
                access,
                userRepository,
                applicationStatusVerifier);
    }

    @Test
    public void testGenerate() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String userId = "user-id";
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getApplication()).thenReturn(application);
        Map<String, Object> details = Collections.emptyMap();
        long timeout = 1000;
        long currentTimestamp = System.currentTimeMillis();
        String key = "key";
        when(access.generateKey(applicationId)).thenReturn(key);
        UserSession entity = service.generate(application, user, details, timeout);
        assertEquals(application, entity.getApplication());
        assertEquals(user, entity.getUser());
        assertEquals(details, entity.getDetails());
        assertEquals(true, (entity.getExpirationTime().getTime() - currentTimestamp >= timeout));
    }

    @Test
    public void testReconstitute() {
        Application application = mock(Application.class);
        User user = mock(User.class);
        UserSessionOS os = mock(UserSessionOS.class);
        UserSessionEntity entity = service.reconstitute(os, application, user);
        assertEquals(application, entity.getApplication());
        assertEquals(user, entity.getUser());
        assertEquals(os, entity.getObjectSegment());
        assertEquals(false, entity.isNew());
        assertEquals(false, entity.isDirty());
        assertEquals(false, entity.isVoid());
    }

    @Test
    public void testReconstitute_NullValue() {
        Application application = mock(Application.class);
        User user = mock(User.class);
        UserSessionEntity entity = service.reconstitute(null, application, user);
        assertEquals(null, entity);
    }

}
