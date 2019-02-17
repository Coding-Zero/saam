package com.codingzero.saam.domain;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.principal.role.RoleRepositoryService;
import com.codingzero.saam.domain.principal.user.UserEntity;
import com.codingzero.saam.domain.principal.user.UserFactoryService;
import com.codingzero.saam.infrastructure.database.UserOS;
import com.codingzero.saam.infrastructure.database.PasswordHelper;
import com.codingzero.saam.infrastructure.database.PrincipalAccess;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PrincipalAccess principalAccess;
    private RoleRepositoryService roleRepository;
    private PasswordHelper passwordHelper;
    private UserFactoryService service;

    @Before
    public void setUp() {
        principalAccess = mock(PrincipalAccess.class);
        roleRepository = mock(RoleRepositoryService.class);
        passwordHelper = mock(PasswordHelper.class);
        service = new UserFactoryService(
                principalAccess,
                roleRepository,
                passwordHelper, applicationStatusVerifier);
    }

    @Test
    public void testGenerate() {
        String principalId = "principalId";
        String applicationId = "appId";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(principalAccess.generateId(applicationId, PrincipalType.USER)).thenReturn(principalId);
        UserEntity entity = service.generate(application);
        assertEquals(application, entity.getApplication());
        assertEquals(principalId, entity.getId());
        assertEquals(true, entity.isNew());
    }

    @Test
    public void testReconstitute() {
        Application application = mock(Application.class);
        UserOS os = mock(UserOS.class);
        UserEntity entity = service.reconstitute(os, application);
        assertEquals(application, entity.getApplication());
        assertEquals(os, entity.getObjectSegment());
    }

    @Test
    public void testReconstitute_NullValue() {
        Application application = mock(Application.class);
        UserEntity entity = service.reconstitute(null, application);
        assertEquals(null, entity);
    }

}
