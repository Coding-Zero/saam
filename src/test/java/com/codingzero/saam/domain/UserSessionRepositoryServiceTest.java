package com.codingzero.saam.domain;

import com.codingzero.saam.domain.services.ApplicationStatusVerifier;
import com.codingzero.saam.domain.usersession.UserSessionEntity;
import com.codingzero.saam.domain.usersession.UserSessionFactoryService;
import com.codingzero.saam.domain.usersession.UserSessionRepositoryService;
import com.codingzero.saam.infrastructure.data.UserSessionAccess;
import com.codingzero.saam.infrastructure.data.UserSessionOS;
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

public class UserSessionRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserSessionAccess access;
    private UserSessionFactoryService factory;
    private ApplicationStatusVerifier applicationStatusVerifier;
    private UserSessionRepositoryService service;

    @Before
    public void setUp() {
        access = mock(UserSessionAccess.class);
        factory = mock(UserSessionFactoryService.class);
        applicationStatusVerifier = mock(ApplicationStatusVerifier.class);
        service = new UserSessionRepositoryService(
                access,
                factory, applicationStatusVerifier);
    }

    @Test
    public void testStore_New() {
        UserSessionEntity entity = mock(UserSessionEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        UserSessionOS os = mock(UserSessionOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
    }

    @Test
    public void testRemove() {
        UserSessionEntity entity = mock(UserSessionEntity.class);
        UserSessionOS os = mock(UserSessionOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(access, times(1)).delete(os);
    }

    @Test
    public void testRemoveByUser() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String userId = "user-id";
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getApplication()).thenReturn(application);
        service.removeByUser(user);
        verify(access, times(1)).deleteByUserId(applicationId, userId);
    }

    @Test
    public void testRemoveAll() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        service.removeByApplication(application);
        verify(access, times(1)).deleteByApplicationId(applicationId);
    }

    @Test
    public void testFindByKey() {
        String key = "key";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        UserSessionOS os = mock(UserSessionOS.class);
        when(access.selectByKey(applicationId, key)).thenReturn(os);
        service.findByKey(application, key);
        verify(factory, times(1)).reconstitute(os, application, null);
    }

    @Test
    public void testFindByOwner() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        String userId = "user-id";
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getApplication()).thenReturn(application);

        ResultPage page = new OffsetBasedResultPage(1, 10);
        UserSessionOS os = mock(UserSessionOS.class);
        List<UserSessionOS> osList = Arrays.asList(os);
        PaginatedResult<List<UserSessionOS>> osResult = mock(PaginatedResult.class);
        when(osResult.getResult()).thenReturn(osList);
        when(osResult.start(page, null)).thenReturn(osResult);
        when(access.selectByUserId(applicationId, userId)).thenReturn(osResult);

        PaginatedResult<List<UserSession>> result = service.findByOwner(user);
        result.start(page).getResult();
        verify(factory, times(osList.size())).reconstitute(os, application, user);
    }
}
