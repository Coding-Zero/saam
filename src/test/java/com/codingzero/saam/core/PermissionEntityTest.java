package com.codingzero.saam.core;

import com.codingzero.saam.common.Action;
import com.codingzero.saam.core.resource.PermissionEntity;
import com.codingzero.saam.core.resource.PermissionFactoryService;
import com.codingzero.saam.core.principal.PrincipalEntity;
import com.codingzero.saam.core.principal.PrincipalRepositoryService;
import com.codingzero.saam.infrastructure.database.PermissionOS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PermissionEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PermissionOS objectSegment;
    private Resource resource;
    private Principal principal;
    private PermissionFactoryService factory;
    private PrincipalRepositoryService principalRepository;
    private PermissionEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(PermissionOS.class);
        resource = mock(Resource.class);
        principal = mock(Principal.class);
        factory = mock(PermissionFactoryService.class);
        principalRepository = mock(PrincipalRepositoryService.class);
        createEntity();
    }

    private void createEntity() {
        entity = new PermissionEntity(
                objectSegment,
                resource,
                principal,
                factory,
                principalRepository);
    }

    @Test
    public void testGetPrincipal() {
        Application application = mock(Application.class);
        when(resource.getApplication()).thenReturn(application);
        String principalId = "principal-id";
        when(objectSegment.getPrincipalId()).thenReturn(principalId);
        PrincipalEntity expectedPrincipal = mock(PrincipalEntity.class);
        when(principalRepository.findById(application, principalId)).thenReturn(expectedPrincipal);
        principal = null;
        createEntity();
        Principal actualPrincipal = entity.getPrincipal();
        assertEquals(expectedPrincipal, actualPrincipal);
    }

    @Test
    public void testGetPrincipal_NotNull() {
        createEntity();
        Principal actualPrincipal = entity.getPrincipal();
        assertEquals(principal, actualPrincipal);
    }

    @Test
    public void testContainAction() {
        String actionRead = "READ";
        String actionWrite = "WRITE";
        List<Action> actions = Arrays.asList(
                new Action(actionRead, false),
                new Action(actionWrite, false)
        );
        when(objectSegment.getActions()).thenReturn(actions);
        createEntity();
        boolean result = entity.containAction(actionWrite);
        assertEquals(true, result);
    }

    @Test
    public void testContainAction_NotFound() {
        String actionRead = "READ";
        String actionWrite = "WRITE";
        List<Action> actions = Arrays.asList(
                new Action(actionRead, false),
                new Action(actionWrite, false)
        );
        when(objectSegment.getActions()).thenReturn(actions);
        createEntity();
        boolean result = entity.containAction("EDIT");
        assertEquals(false, result);
    }

}
