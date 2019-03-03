package com.codingzero.saam.domain;

import com.codingzero.saam.domain.application.ApplicationFactoryService;
import com.codingzero.saam.domain.application.ApplicationRepositoryService;
import com.codingzero.saam.domain.application.ApplicationRoot;
import com.codingzero.saam.domain.application.IdentifierPolicyRepositoryService;
import com.codingzero.saam.domain.application.OAuthIdentifierPolicyRepositoryService;
import com.codingzero.saam.domain.principal.PrincipalRepositoryService;
import com.codingzero.saam.domain.resource.PermissionRepositoryService;
import com.codingzero.saam.domain.resource.ResourceRepositoryService;
import com.codingzero.saam.domain.usersession.UserSessionRepositoryService;
import com.codingzero.saam.infrastructure.data.ApplicationAccess;
import com.codingzero.saam.infrastructure.data.ApplicationOS;
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

public class ApplicationRepositoryServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ApplicationAccess access;
    private ApplicationFactoryService factory;
    private IdentifierPolicyRepositoryService identifierPolicyRepository;
    private OAuthIdentifierPolicyRepositoryService oAuthIdentifierPolicyRepository;
    private PrincipalRepositoryService principalRepository;
    private ResourceRepositoryService resourceRepository;
    private PermissionRepositoryService permissionRepository;
    private UserSessionRepositoryService userSessionRepository;
    private ApplicationRepositoryService service;

    @Before
    public void setUp() {
        access = mock(ApplicationAccess.class);
        factory = mock(ApplicationFactoryService.class);
        identifierPolicyRepository = mock(IdentifierPolicyRepositoryService.class);
        oAuthIdentifierPolicyRepository = mock(OAuthIdentifierPolicyRepositoryService.class);
        principalRepository = mock(PrincipalRepositoryService.class);
        userSessionRepository = mock(UserSessionRepositoryService.class);
        permissionRepository = mock(PermissionRepositoryService.class);
        resourceRepository = mock(ResourceRepositoryService.class);
        service = new ApplicationRepositoryService(
                access,
                factory,
                identifierPolicyRepository,
                oAuthIdentifierPolicyRepository
        );
    }

    @Test
    public void testStore_New() {
        ApplicationRoot entity = mock(ApplicationRoot.class);
        when(entity.isNew()).thenReturn(true);
        when(entity.isDirty()).thenReturn(true);
        ApplicationOS os = mock(ApplicationOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(1)).insert(os);
        verify(access, times(0)).update(os);
        verifyFlushDirtyEntities(entity);
    }

    private void verifyFlushDirtyEntities(ApplicationRoot entity) {
        verify(entity, times(1)).getDirtyIdentifierPolicies();
        verify(entity, times(1)).getDirtyOAuthIdentifierPolicies();
    }

    @Test
    public void testStore_Update() {
        ApplicationRoot entity = mock(ApplicationRoot.class);
        when(entity.isNew()).thenReturn(false);
        when(entity.isDirty()).thenReturn(true);
        ApplicationOS os = mock(ApplicationOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.store(entity);
        verify(access, times(0)).insert(os);
        verify(access, times(1)).update(os);
        verifyFlushDirtyEntities(entity);
    }

    @Test
    public void testRemove() {
        ApplicationRoot entity = mock(ApplicationRoot.class);
        ApplicationOS os = mock(ApplicationOS.class);
        when(entity.getObjectSegment()).thenReturn(os);
        service.remove(entity);
        verify(identifierPolicyRepository, times(1)).removeAll(entity);
        verify(oAuthIdentifierPolicyRepository, times(1)).removeAll(entity);
        verify(userSessionRepository, times(1)).removeByApplication(entity);
        verify(resourceRepository, times(1)).removeByApplication(entity);
        verify(principalRepository, times(1)).removeAll(entity);
        verify(access, times(1)).delete(os);
    }

    @Test
    public void testFindById() {
        String applicationId = "application-id";
        ApplicationOS os = mock(ApplicationOS.class);
        when(access.selectById(applicationId)).thenReturn(os);
        service.findById(applicationId);
        verify(factory, times(1)).reconstitute(os);
    }

    @Test
    public void testFindAll() {
        ResultPage page = new OffsetBasedResultPage(1, 10);
        ApplicationOS os = mock(ApplicationOS.class);
        List<ApplicationOS> osList = Arrays.asList(os);
        PaginatedResult<List<ApplicationOS>> osResult = mock(PaginatedResult.class);
        when(osResult.getResult()).thenReturn(osList);
        when(osResult.start(page, null)).thenReturn(osResult);
        when(access.selectAll()).thenReturn(osResult);
        PaginatedResult<List<Application>> result = service.findAll();
        result.start(page).getResult();
        verify(factory, times(osList.size())).reconstitute(os);
    }

}
