package com.codingzero.saam.domain;

import com.codingzero.saam.common.IdentifierKey;
import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.domain.identifier.IdentifierEntity;
import com.codingzero.saam.domain.identifier.IdentifierFactoryService;
import com.codingzero.saam.domain.identifier.IdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.IdentifierAccess;
import com.codingzero.saam.infrastructure.database.IdentifierOS;
import com.codingzero.utilities.pagination.OffsetBasedResultPage;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.pagination.ResultPage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IdentifierRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IdentifierAccess access;
    private IdentifierFactoryService factory;
    private IdentifierRepositoryService service;

    @Before
    public void setUp() {
        access = mock(IdentifierAccess.class);
        factory = mock(IdentifierFactoryService.class);
        service = new IdentifierRepositoryService(
                access,
                factory);
    }

    @Test
    public void testStore_New() {
        IdentifierEntity entity = mock(IdentifierEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        IdentifierOS os = mock(IdentifierOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        IdentifierEntity entity = mock(IdentifierEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        IdentifierOS os = mock(IdentifierOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        IdentifierEntity entity = mock(IdentifierEntity.class);
        IdentifierOS os = mock(IdentifierOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(access, times(1)).delete(os);
    }

    @Test
    public void testFindByContent() {
        IdentifierType type = IdentifierType.USERNAME;
        String content = "content";
        String applicationId = "application-id";
        IdentifierKey key = new IdentifierKey(applicationId, content);
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getType()).thenReturn(type);
        IdentifierOS os = mock(IdentifierOS.class);
        when(access.selectByKey(key)).thenReturn(os);
        service.findByKey(application, content);
        verify(factory, times(1)).reconstitute(os, application, null);
    }

    @Test
    public void testFindByPolicyAndUser() {
        IdentifierType type = IdentifierType.USERNAME;
        String applicationId = "application-id";
        String userId = "user-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getType()).thenReturn(type);
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        IdentifierOS os = mock(IdentifierOS.class);
        List<IdentifierOS> osList = Arrays.asList(os);
        when(access.selectByUserId(applicationId, userId)).thenReturn(osList);
        service.findByUser(application, user);
        verify(factory, times(osList.size())).reconstitute(os, application, user);
    }

    @Test
    public void testFindByPolicy() {
        IdentifierType type = IdentifierType.USERNAME;
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        IdentifierPolicy policy = mock(IdentifierPolicy.class);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getType()).thenReturn(type);

        ResultPage page = new OffsetBasedResultPage(1, 10);
        IdentifierOS os = mock(IdentifierOS.class);
        List<IdentifierOS> osList = Arrays.asList(os);
        PaginatedResult<List<IdentifierOS>> osResult = mock(PaginatedResult.class);
        when(osResult.getResult()).thenReturn(osList);
        when(osResult.start(page, null)).thenReturn(osResult);
        when(access.selectByType(applicationId, type)).thenReturn(osResult);

        PaginatedResult<List<Identifier>> result = service.findByIdentifierType(application, type);
        result.start(page).getResult();
        verify(factory, times(osList.size())).reconstitute(os, application, null);
    }

}
