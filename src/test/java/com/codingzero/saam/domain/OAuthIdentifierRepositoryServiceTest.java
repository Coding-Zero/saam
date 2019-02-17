package com.codingzero.saam.domain;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.domain.oauthidentifier.OAuthIdentifierEntity;
import com.codingzero.saam.domain.oauthidentifier.OAuthIdentifierFactoryService;
import com.codingzero.saam.domain.oauthidentifier.OAuthIdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierOS;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierAccess;
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

public class OAuthIdentifierRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthIdentifierAccess access;
    private OAuthIdentifierFactoryService factory;
    private OAuthIdentifierRepositoryService service;

    @Before
    public void setUp() {
        access = mock(OAuthIdentifierAccess.class);
        factory = mock(OAuthIdentifierFactoryService.class);
        service = new OAuthIdentifierRepositoryService(
                access,
                factory);
    }

    @Test
    public void testStore_New() {
        OAuthIdentifierEntity entity = mock(OAuthIdentifierEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        OAuthIdentifierOS os = mock(OAuthIdentifierOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        OAuthIdentifierEntity entity = mock(OAuthIdentifierEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        OAuthIdentifierOS os = mock(OAuthIdentifierOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        OAuthIdentifierEntity entity = mock(OAuthIdentifierEntity.class);
        OAuthIdentifierOS os = mock(OAuthIdentifierOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(access, times(1)).delete(os);
    }

    @Test
    public void testFindByContent() {
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        String content = "content";
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        OAuthIdentifierPolicy policy = mock(OAuthIdentifierPolicy.class);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getPlatform()).thenReturn(platform);
        OAuthIdentifierOS os = mock(OAuthIdentifierOS.class);
        when(access.selectByKey(applicationId)).thenReturn(os);
        service.findByContent(policy, content);
        verify(factory, times(1)).reconstitute(os, policy, null);
    }

    @Test
    public void testFindByPolicyAndUser() {
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        String applicationId = "application-id";
        String userId = "user-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        OAuthIdentifierPolicy policy = mock(OAuthIdentifierPolicy.class);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getPlatform()).thenReturn(platform);
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        OAuthIdentifierOS os = mock(OAuthIdentifierOS.class);
        List<OAuthIdentifierOS> osList = Arrays.asList(os);
        when(access.selectByUserId(applicationId, userId)).thenReturn(osList);
        service.findByPolicyAndUser(policy, user);
        verify(factory, times(osList.size())).reconstitute(os, policy, user);
    }

    @Test
    public void testFindByPolicy() {
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        OAuthIdentifierPolicy policy = mock(OAuthIdentifierPolicy.class);
        when(policy.getApplication()).thenReturn(application);
        when(policy.getPlatform()).thenReturn(platform);

        ResultPage page = new OffsetBasedResultPage(1, 10);
        OAuthIdentifierOS os = mock(OAuthIdentifierOS.class);
        List<OAuthIdentifierOS> osList = Arrays.asList(os);
        PaginatedResult<List<OAuthIdentifierOS>> osResult = mock(PaginatedResult.class);
        when(osResult.getResult()).thenReturn(osList);
        when(osResult.start(page, null)).thenReturn(osResult);
        when(access.selectByPlatform(applicationId, platform)).thenReturn(osResult);

        PaginatedResult<List<OAuthIdentifier>> result = service.findByPolicy(policy);
        result.start(page).getResult();
        verify(factory, times(osList.size())).reconstitute(os, policy, null);
    }

}
