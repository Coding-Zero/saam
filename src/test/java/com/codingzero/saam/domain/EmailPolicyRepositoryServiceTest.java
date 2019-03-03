package com.codingzero.saam.domain;

import com.codingzero.saam.domain.application.EmailPolicyEntity;
import com.codingzero.saam.domain.application.EmailPolicyFactoryService;
import com.codingzero.saam.domain.application.EmailPolicyRepositoryService;
import com.codingzero.saam.infrastructure.data.EmailPolicyOS;
import com.codingzero.saam.infrastructure.data.IdentifierPolicyOS;
import com.codingzero.saam.infrastructure.data.EmailPolicyAccess;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailPolicyRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private EmailPolicyAccess access;
    private EmailPolicyFactoryService factory;
    private EmailPolicyRepositoryService service;

    @Before
    public void setUp() {
        access = mock(EmailPolicyAccess.class);
        factory = mock(EmailPolicyFactoryService.class);
        service = new EmailPolicyRepositoryService(
                access,
                factory);
    }

    @Test
    public void testStore_New() {
        EmailPolicyEntity entity = mock(EmailPolicyEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        EmailPolicyOS os = mock(EmailPolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        EmailPolicyEntity entity = mock(EmailPolicyEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        EmailPolicyOS os = mock(EmailPolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        EmailPolicyEntity entity = mock(EmailPolicyEntity.class);
        EmailPolicyOS os = mock(EmailPolicyOS.class);
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
        EmailPolicyOS os = mock(EmailPolicyOS.class);
        when(access.selectByIdentifierPolicyOS(identifierPolicyOS)).thenReturn(os);
        service.load(application, identifierPolicyOS);
        verify(factory, times(1)).reconstitute(os, application);
    }

}
