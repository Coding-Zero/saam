package com.codingzero.saam.domain;

import com.codingzero.saam.common.OAuthPlatform;
import com.codingzero.saam.domain.oauthidentifier.OAuthIdentifierFactoryService;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyEntity;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyFactoryService;
import com.codingzero.saam.domain.oauthidentifier.OAuthIdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyOS;
import com.codingzero.saam.infrastructure.database.OAuthIdentifierPolicyAccess;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OAuthIdentifierPolicyFactoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OAuthIdentifierPolicyAccess access;
    private OAuthIdentifierFactoryService identifierFactory;
    private OAuthIdentifierRepositoryService identifierRepository;
    private OAuthIdentifierPolicyFactoryService service;

    @Before
    public void setUp() {
        access = mock(OAuthIdentifierPolicyAccess.class);
        identifierFactory = mock(OAuthIdentifierFactoryService.class);
        identifierRepository = mock(OAuthIdentifierRepositoryService.class);
        service = new OAuthIdentifierPolicyFactoryService(
                access,
                identifierFactory,
                identifierRepository);
    }

    @Test
    public void testGenerate() {
        Application application = mock(Application.class);
        when(application.getId()).thenReturn("APP_1");
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        Map<String, Object> configurations = new HashMap<>();
        when(access.isDuplicatePlatform("APP_1", platform)).thenReturn(false);
        OAuthIdentifierPolicyEntity entity =
                service.generate(application, platform, configurations);
        assertEquals(application, entity.getApplication());
        assertEquals(OAuthPlatform.GOOGLE, entity.getPlatform());
        assertEquals(configurations, entity.getConfigurations());
        assertNewPolicy(entity);
    }

    private void assertNewPolicy(OAuthIdentifierPolicyEntity policy) {
        assertEquals(policy.getCreationTime(), policy.getUpdateTime());
        assertEquals(true, policy.isNew());
        assertEquals(false, policy.isDirty());
        assertEquals(false, policy.isVoid());
    }

    @Test
    public void testGenerate_DuplicatePlatform() {
        Application application = mock(Application.class);
        when(application.getId()).thenReturn("APP_1");
        OAuthPlatform platform = OAuthPlatform.GOOGLE;
        Map<String, Object> configurations = new HashMap<>();
        when(access.isDuplicatePlatform("APP_1", platform)).thenReturn(true);
        thrown.expect(BusinessError.class);
        service.generate(application, platform, configurations);
    }

    @Test
    public void testReconstitute() {
        Application application = mock(Application.class);
        OAuthIdentifierPolicyOS os = mock(OAuthIdentifierPolicyOS.class);
        OAuthIdentifierPolicyEntity entity = service.reconstitute(os, application);
        assertEquals(application, entity.getApplication());
        assertEquals(os, entity.getObjectSegment());
    }

    @Test
    public void testReconstitute_NullValue() {
        Application application = mock(Application.class);
        OAuthIdentifierPolicyEntity entity = service.reconstitute(null, application);
        assertEquals(null, entity);
    }

}
