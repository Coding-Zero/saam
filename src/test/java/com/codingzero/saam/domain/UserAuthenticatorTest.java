package com.codingzero.saam.domain;

import com.codingzero.saam.domain.services.UserAuthenticator;
import com.codingzero.utilities.error.BusinessError;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAuthenticatorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserSessionFactory userSessionFactory;
    private UserAuthenticator service;

    @Before
    public void setUp() {
        userSessionFactory = mock(UserSessionFactory.class);
        service = new UserAuthenticator(userSessionFactory);
    }

    @Test
    public void testLogin() {
        long timeout = 1000;
        Map<String, Object> details = Collections.emptyMap();
        UserSession userSession = mock(UserSession.class);
        User user = mock(User.class);
        Application application = mock(Application.class);
        when(userSessionFactory.generate(application, user, details, timeout)).thenReturn(userSession);
        when(user.getApplication()).thenReturn(application);
        UserSession actualSession = service.login(user, details, timeout);
        assertEquals(userSession, actualSession);
    }

    @Test
    public void testLogin_Credential() {
        String password = "password";
        long timeout = 1000;
        Map<String, Object> details = Collections.emptyMap();
        UserSession userSession = mock(UserSession.class);
        User user = mock(User.class);
        when(user.verifyPassword(password)).thenReturn(true);
        Application application = mock(Application.class);
        when(userSessionFactory.generate(application, user, details, timeout)).thenReturn(userSession);
        when(user.getApplication()).thenReturn(application);
        UserSession actualSession = service.login(user, password, details, timeout);
        assertEquals(userSession, actualSession);
    }

    @Test
    public void testLogin_NullValue() {
        String password = "password";
        long timeout = 1000;
        Map<String, Object> details = Collections.emptyMap();
        thrown.expect(BusinessError.class);
        service.login(null, password, details, timeout);
    }

    @Test
    public void testLogin_InvalidPassword() {
        String password = "password";
        long timeout = 1000;
        Map<String, Object> details = Collections.emptyMap();
        User user = mock(User.class);
        when(user.verifyPassword(password)).thenReturn(false);
        thrown.expect(BusinessError.class);
        service.login(user, password, details, timeout);
    }

}
