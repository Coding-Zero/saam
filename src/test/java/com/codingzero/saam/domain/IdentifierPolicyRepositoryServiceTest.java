package com.codingzero.saam.domain;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.domain.application.EmailPolicyEntity;
import com.codingzero.saam.domain.application.EmailPolicyRepositoryService;
import com.codingzero.saam.domain.application.IdentifierPolicyEntity;
import com.codingzero.saam.domain.application.IdentifierPolicyRepositoryService;
import com.codingzero.saam.domain.identifier.IdentifierRepositoryService;
import com.codingzero.saam.domain.application.UsernamePolicyEntity;
import com.codingzero.saam.domain.application.UsernamePolicyRepositoryService;
import com.codingzero.saam.infrastructure.database.EmailPolicyOS;
import com.codingzero.saam.infrastructure.database.IdentifierAccess;
import com.codingzero.saam.infrastructure.database.UsernamePolicyOS;
import com.codingzero.saam.infrastructure.database.IdentifierPolicyAccess;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IdentifierPolicyRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierPolicyAccess access;
    private EmailPolicyRepositoryService emailIdentifierPolicyRepository;
    private UsernamePolicyRepositoryService usernameIdentifierPolicyRepository;
    private IdentifierAccess identifierAccess;
    private IdentifierPolicyRepositoryService service;

    @Before
    public void setUp() {
        access = mock(IdentifierPolicyAccess.class);
        emailIdentifierPolicyRepository = mock(EmailPolicyRepositoryService.class);
        usernameIdentifierPolicyRepository = mock(UsernamePolicyRepositoryService.class);
        identifierAccess = mock(IdentifierAccess.class);
        service = new IdentifierPolicyRepositoryService(
                access,
                emailIdentifierPolicyRepository,
                usernameIdentifierPolicyRepository,
                identifierAccess);
    }

    @Test
    public void testStore_New_Username() {
        UsernamePolicyEntity entity = mock(UsernamePolicyEntity.class);
        when(entity.getType()).thenReturn(IdentifierType.USERNAME);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(usernameIdentifierPolicyRepository, times(1)).store(entity);
    }

    @Test
    public void testStore_Update_Username() {
        UsernamePolicyEntity entity = mock(UsernamePolicyEntity.class);
        when(entity.getType()).thenReturn(IdentifierType.USERNAME);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(usernameIdentifierPolicyRepository, times(1)).store(entity);
    }

    @Test
    public void testStore_New_Email() {
        EmailPolicyEntity entity = mock(EmailPolicyEntity.class);
        when(entity.getType()).thenReturn(IdentifierType.EMAIL);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        EmailPolicyOS os = mock(EmailPolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(emailIdentifierPolicyRepository, times(1)).store(entity);
    }

    @Test
    public void testStore_Update_Email() {
        EmailPolicyEntity entity = mock(EmailPolicyEntity.class);
        when(entity.getType()).thenReturn(IdentifierType.EMAIL);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        EmailPolicyOS os = mock(EmailPolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(emailIdentifierPolicyRepository, times(1)).store(entity);
    }

    @Test
    public void testRemove_Username() {
        UsernamePolicyEntity entity = mock(UsernamePolicyEntity.class);
        when(entity.getType()).thenReturn(IdentifierType.USERNAME);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(usernameIdentifierPolicyRepository, times(1)).remove(entity);
    }

    @Test
    public void testRemove_Email() {
        EmailPolicyEntity entity = mock(EmailPolicyEntity.class);
        when(entity.getType()).thenReturn(IdentifierType.EMAIL);
        EmailPolicyOS os = mock(EmailPolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(emailIdentifierPolicyRepository, times(1)).remove(entity);
    }

    @Test
    public void testRemoveAll() {
        String applicationId = "application-id";
        Application entity = mock(Application.class);
        when(entity.getId()).thenReturn(applicationId);
        service.removeAll(entity);
        verify(usernameIdentifierPolicyRepository, times(1)).removeAll(entity);
        verify(emailIdentifierPolicyRepository, times(1)).removeAll(entity);
    }

    @Test
    public void testFindByType_Username() {
        IdentifierType type = IdentifierType.USERNAME;
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        UsernamePolicyOS os = mock(UsernamePolicyOS.class);
        when(os.getType()).thenReturn(type);
        when(access.selectByType(applicationId, type)).thenReturn(os);
        service.findByType(application, type);
        verify(usernameIdentifierPolicyRepository, times(1)).load(application, os);
    }

    @Test
    public void testFindByType_Username_NotExist() {
        IdentifierType type = IdentifierType.USERNAME;
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(access.selectByType(applicationId, type)).thenReturn(null);
        IdentifierPolicyEntity entity = service.findByType(application, type);
        assertNull(entity);
    }

    @Test
    public void testFindByType_Email() {
        IdentifierType type = IdentifierType.EMAIL;
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        EmailPolicyOS os = mock(EmailPolicyOS.class);
        when(os.getType()).thenReturn(type);
        when(access.selectByType(applicationId, type)).thenReturn(os);
        service.findByType(application, type);
        verify(emailIdentifierPolicyRepository, times(1)).load(application, os);
    }

    @Test
    public void testFindByType_Email_NotExist() {
        IdentifierType type = IdentifierType.EMAIL;
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        when(access.selectByType(applicationId, type)).thenReturn(null);
        IdentifierPolicyEntity entity = service.findByType(application, type);
        assertNull(entity);
    }

}
