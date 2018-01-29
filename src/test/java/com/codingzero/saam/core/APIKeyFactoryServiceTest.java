package com.codingzero.saam.core;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.core.application.APIKeyEntity;
import com.codingzero.saam.core.application.APIKeyFactoryService;
import com.codingzero.saam.core.application.UserRepositoryService;
import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.saam.infrastructure.database.spi.APIKeyAccess;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class APIKeyFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private APIKeyAccess access;
    private PrincipalAccess principalAccess;
    private UserRepositoryService userRepository;
    private APIKeyFactoryService service;

    @Before
    public void setUp() {
        access = mock(APIKeyAccess.class);
        principalAccess = mock(PrincipalAccess.class);
        userRepository = mock(UserRepositoryService.class);
        service = new APIKeyFactoryService(
                access,
                principalAccess,
                userRepository);
    }

    @Test
    public void testGenerate() {
        String name = "name";
        String key = "key";
        String principalId = "principalId";
        String applicationId = "appId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        User user = mock(User.class);
        when(principalAccess.generateId(applicationId, PrincipalType.API_KEY)).thenReturn(principalId);
        when(access.generateKey()).thenReturn(key);
        APIKeyEntity entity = service.generate(application, user, name);
        assertEquals(application, entity.getApplication());
        assertEquals(principalId, entity.getId());
        assertEquals(key, entity.getKey());
        assertEquals(user, entity.getOwner());
        assertEquals(true, entity.isNew());
    }

    @Test
    public void testCheckForNameFormat() {
        String name = "name";
        service.checkForNameFormat(name);
    }

    @Test
    public void testCheckForNameFormat_NullValue() {
        thrown.expect(IllegalArgumentException.class);
        service.checkForNameFormat(null);
    }

    @Test
    public void testCheckForNameFormat_TooShort() {
        String name = "a";
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(name);
    }

    @Test
    public void testCheckForNameFormat_TooLong() {
        StringBuilder roleName = new StringBuilder();
        for (int i = 0; i < APIKeyFactoryService.NAME_MAX_LENGTH; i ++) {
            roleName.append("a");
        }
        roleName.append("a");
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(roleName.toString());
    }

    @Test
    public void testCheckForNameFormat_InvalidFormat() {
        String roleName = "a~b";
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(roleName);
    }

    @Test
    public void testReconstitute() {
        Application application = mock(Application.class);
        APIKeyOS os = mock(APIKeyOS.class);
        User user = mock(User.class);
        APIKeyEntity entity = service.reconstitute(os, application, user);
        assertEquals(application, entity.getApplication());
        assertEquals(user, entity.getOwner());
        assertEquals(os, entity.getObjectSegment());
    }

    @Test
    public void testReconstitute_NullValue() {
        Application application = mock(Application.class);
        User user = mock(User.class);
        APIKeyEntity entity = service.reconstitute(null, application, user);
        assertEquals(null, entity);
    }

}
