package com.codingzero.saam.domain;

import com.codingzero.saam.domain.application.EmailPolicyEntity;
import com.codingzero.saam.domain.application.EmailPolicyFactoryService;
import com.codingzero.saam.domain.identifier.IdentifierFactoryService;
import com.codingzero.saam.domain.identifier.IdentifierRepositoryService;
import com.codingzero.saam.infrastructure.database.EmailPolicyOS;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailPolicyEntityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private EmailPolicyOS objectSegment;
    private Application application;
    private EmailPolicyFactoryService factory;
    private IdentifierFactoryService identifierFactory;
    private IdentifierRepositoryService identifierRepository;
    private EmailPolicyEntity entity;

    @Before
    public void setUp() {
        objectSegment = mock(EmailPolicyOS.class);
        application = mock(Application.class);
        factory = mock(EmailPolicyFactoryService.class);
        identifierFactory = mock(IdentifierFactoryService.class);
        identifierRepository = mock(IdentifierRepositoryService.class);
        entity = new EmailPolicyEntity(
                objectSegment,
                application,
                factory,
                identifierFactory,
                identifierRepository);
    }

    @Test
    public void testSetDomain() {
        List<String> domains = Arrays.asList(
                "foo1.com",
                "Foo2.com",
                "foo3.com");
        when(factory.deduplicateDomains(domains)).thenReturn(domains);
        when(objectSegment.getDomains()).thenReturn(new ArrayList<>());
        entity.setDomains(domains);
        verify(objectSegment, times(1)).setDomains(domains);
        verify(objectSegment,times(1)).setUpdateTime(any(Date.class));
        assertEquals(true, entity.isDirty());
    }

    @Test
    public void testSetDomain_SameValue() {
        List<String> domains = Arrays.asList(
                "foo1.com",
                "Foo2.com",
                "foo3.com");
        when(factory.deduplicateDomains(domains)).thenReturn(domains);
        when(objectSegment.getDomains()).thenReturn(domains);
        entity.setDomains(domains);
        verify(objectSegment, times(0)).setDomains(domains);
        verify(objectSegment,times(0)).setUpdateTime(any(Date.class));
        assertEquals(false, entity.isDirty());
    }

    @Test
    public void testSetMinLength() {
        thrown.expect(UnsupportedOperationException.class);
        entity.setMinLength(1);
    }

    @Test
    public void testSetMaxLength() {
        thrown.expect(UnsupportedOperationException.class);
        entity.setMaxLength(1);
    }

    @Test
    public void testCheck() {
        when(objectSegment.getDomains()).thenReturn(new ArrayList<>());
        when(objectSegment.getMinLength()).thenReturn(EmailPolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(EmailPolicyFactoryService.MAX_LENGTH);
        entity.check("foo@foo.com");
    }

    @Test
    public void testCheck_Length_TooShort() {
        when(objectSegment.getDomains()).thenReturn(new ArrayList<>());
        when(objectSegment.getMinLength()).thenReturn(EmailPolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(EmailPolicyFactoryService.MAX_LENGTH);
        thrown.expect(BusinessError.class);
        entity.check("f@f.c");
    }

    @Test
    public void testCheck_Length_TooLong() {
        StringBuilder identifier = new StringBuilder();
        for (int i = 0; i < EmailPolicyFactoryService.MAX_LENGTH; i ++) {
            identifier.append("a");
        }
        identifier.append("@foo.com");
        when(objectSegment.getDomains()).thenReturn(new ArrayList<>());
        when(objectSegment.getMinLength()).thenReturn(EmailPolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(EmailPolicyFactoryService.MAX_LENGTH);
        thrown.expect(BusinessError.class);
        entity.check(identifier.toString());
    }

    @Test
    public void setCheck_IllegalFormat() {
        when(objectSegment.getDomains()).thenReturn(new ArrayList<>());
        when(objectSegment.getMinLength()).thenReturn(EmailPolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(EmailPolicyFactoryService.MAX_LENGTH);
        thrown.expect(BusinessError.class);
        entity.check("foo@com");
    }

    @Test
    public void setCheck_Domains() {
        List<String> domains = Arrays.asList(
                "foo.com");
        when(objectSegment.getDomains()).thenReturn(domains);
        when(objectSegment.getMinLength()).thenReturn(EmailPolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(EmailPolicyFactoryService.MAX_LENGTH);
        entity.check("foo@foo.com");
    }

    @Test
    public void setCheck_Domains_NotAllowed() {
        List<String> domains = Arrays.asList(
                "foo.com",
                "foo1.com");
        when(objectSegment.getDomains()).thenReturn(domains);
        when(objectSegment.getMinLength()).thenReturn(EmailPolicyFactoryService.MIN_LENGTH);
        when(objectSegment.getMaxLength()).thenReturn(EmailPolicyFactoryService.MAX_LENGTH);
        thrown.expect(BusinessError.class);
        entity.check("foo@fff.com");
    }

}
