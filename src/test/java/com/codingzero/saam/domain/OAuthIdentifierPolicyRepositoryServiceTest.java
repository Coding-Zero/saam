package com.codingzero.saam.domain;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyEntity;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyFactoryService;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyRepositoryService;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyAccess;
import com.codingzero.saam.infrastructure.data.OAuthIdentifierPolicyOS;
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

public class OAuthIdentifierPolicyRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthIdentifierPolicyAccess access;
    private OAuthIdentifierPolicyFactoryService factory;
    private OAuthIdentifierAccess oAuthIdentifierAccess;
    private OAuthIdentifierPolicyRepositoryService service;

    @Before
    public void setUp() {
        access = mock(OAuthIdentifierPolicyAccess.class);
        factory = mock(OAuthIdentifierPolicyFactoryService.class);
        oAuthIdentifierAccess = mock(OAuthIdentifierAccess.class);
        service = new OAuthIdentifierPolicyRepositoryService(
                access,
                factory,
                oAuthIdentifierAccess);
    }

    @Test
    public void testStore_New() {
        OAuthIdentifierPolicyEntity entity = mock(OAuthIdentifierPolicyEntity.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        OAuthIdentifierPolicyOS os = mock(OAuthIdentifierPolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
    }

    @Test
    public void testStore_Update() {
        OAuthIdentifierPolicyEntity entity = mock(OAuthIdentifierPolicyEntity.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        OAuthIdentifierPolicyOS os = mock(OAuthIdentifierPolicyOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
    }

    @Test
    public void testRemove() {
        OAuthIdentifierPolicyEntity entity = mock(OAuthIdentifierPolicyEntity.class);
        OAuthIdentifierPolicyOS os = mock(OAuthIdentifierPolicyOS.class);
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
    public void testFindByPlatform() {
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        OAuthIdentifierPolicyOS os = mock(OAuthIdentifierPolicyOS.class);
        when(access.selectByPlatform(applicationId, platform)).thenReturn(os);
        service.findByPlatform(application, platform);
        verify(factory, times(1)).reconstitute(os, application);
    }

    @Test
    public void testFindAll() {
        String applicationId = "application-id";
        Application application = mock(Application.class);
        when(application.getId()).thenReturn(applicationId);
        OAuthIdentifierPolicyOS os = mock(OAuthIdentifierPolicyOS.class);
        List<OAuthIdentifierPolicyOS> osList = Arrays.asList(os);
        when(access.selectByApplicationId(applicationId)).thenReturn(osList);
        service.findAll(application);
        verify(factory, times(osList.size())).reconstitute(os, application);
    }

}