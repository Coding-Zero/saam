package com.codingzero.saam.domain;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.domain.principal.role.RoleEntity;
import com.codingzero.saam.domain.principal.role.RoleFactoryService;
import com.codingzero.saam.infrastructure.database.RoleOS;
import com.codingzero.saam.infrastructure.database.PrincipalAccess;
import com.codingzero.saam.infrastructure.database.RoleAccess;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoleFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private RoleAccess access;
    private PrincipalAccess principalAccess;
    private RoleFactoryService service;

    @Before
    public void setUp() {
        access = mock(RoleAccess.class);
        principalAccess = mock(PrincipalAccess.class);
        service = new RoleFactoryService(
                access,
                principalAccess, applicationStatusVerifier);
    }

    @Test
    public void testGenerate() {
        String principalId = "principalId";
        String applicationId = "appId";
        String roleName = "role1";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(principalAccess.generateId(applicationId, PrincipalType.ROLE)).thenReturn(principalId);
        RoleEntity entity = service.generate(application, roleName);
        assertEquals(application, entity.getApplication());
        assertEquals(principalId, entity.getId());
        assertEquals(roleName, entity.getName());
        assertEquals(true, entity.isNew());
    }

    @Test
    public void testCheckForDuplicateName() {
        String applicationId = "appId";
        String roleName = "role1";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(access.isDuplicateName(applicationId, roleName)).thenReturn(false);
        service.checkForDuplicateName(application, roleName);
    }

    @Test
    public void testCheckForDuplicateName_Duplicate() {
        String applicationId = "appId";
        String roleName = "role1";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(access.isDuplicateName(applicationId, roleName)).thenReturn(true);
        thrown.expect(BusinessError.class);
        service.checkForDuplicateName(application, roleName);
    }

    @Test
    public void testCheckForNameFormat() {
        String roleName = "role1";
        service.checkForNameFormat(roleName);
    }

    @Test
    public void testCheckForNameFormat_NullValue() {
        thrown.expect(IllegalArgumentException.class);
        service.checkForNameFormat(null);
    }

    @Test
    public void testCheckForNameFormat_TooShort() {
        String roleName = "a";
        thrown.expect(BusinessError.class);
        service.checkForNameFormat(roleName);
    }

    @Test
    public void testCheckForNameFormat_TooLong() {
        StringBuilder roleName = new StringBuilder();
        for (int i = 0; i < RoleFactoryService.NAME_MAX_LENGTH; i ++) {
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
        RoleOS os = mock(RoleOS.class);
        RoleEntity entity = service.reconstitute(os, application);
        assertEquals(application, entity.getApplication());
        assertEquals(os, entity.getObjectSegment());
    }

    @Test
    public void testReconstitute_NullValue() {
        Application application = mock(Application.class);
        RoleEntity entity = service.reconstitute(null, application);
        assertEquals(null, entity);
    }

}
