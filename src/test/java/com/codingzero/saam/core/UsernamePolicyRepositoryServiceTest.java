package com.codingzero.saam.core;

import com.codingzero.saam.core.application.UsernamePolicyEntity;
import com.codingzero.saam.core.application.UsernamePolicyFactoryService;
import com.codingzero.saam.core.application.UsernamePolicyRepositoryService;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.UsernamePolicyOS;
import com.codingzero.saam.infrastructure.database.spi.UsernamePolicyAccess;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsernamePolicyRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UsernamePolicyAccess access;
    private UsernamePolicyFactoryService factory;
    private UsernamePolicyRepositoryService service;

    @Before
    public void setUp() {
        access = mock(UsernamePolicyAccess.class);
        factory = mock(UsernamePolicyFactoryService.class);
        service = new UsernamePolicyRepositoryService(
                access,
                factory);
    }

    @Test
    public void testStore_New() {
        UsernamePolicyEntity entity = mock(UsernamePolicyEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        UsernamePolicyEntity entity = mock(UsernamePolicyEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        UsernamePolicyEntity entity = mock(UsernamePolicyEntity.class);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(access, times(1)).delete(os);
    }

    @Test
    public void testRemoveAll() {
        String applicationId = "application-id";
        Application entity = mock(Application.class);
        when(entity.getId()).thenReturn(applicationId);
        service.removeAll(entity);
        verify(access, times(1)).deleteByApplicationId(applicationId);
    }

    @Test
    public void testLoad() {
        Application application = mock(Application.class);
        IdentifierPolicyOS identifierPolicyOS = mock(IdentifierPolicyOS.class);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        when(access.selectByIdentifierPolicyOS(identifierPolicyOS)).thenReturn(os);
        service.load(application, identifierPolicyOS);
        verify(factory, times(1)).reconstitute(os, application);
    }

}
