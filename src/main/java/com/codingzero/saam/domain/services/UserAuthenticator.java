package com.codingzero.saam.domain.services;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.domain.Application;
import com.codingzero.saam.domain.User;
import com.codingzero.saam.domain.UserSession;
import com.codingzero.saam.domain.UserSessionFactory;
import com.codingzero.utilities.error.BusinessError;

import java.util.Map;

public class UserAuthenticator {

    private UserSessionFactory userSessionFactory;

    public UserAuthenticator(UserSessionFactory userSessionFactory) {
        this.userSessionFactory = userSessionFactory;
    }

    public UserSession login(User user, String password, Map<String, Object> details, long timeout) {
        checkNullValue(user);
        verifyPassword(user, password);
        return getUserSession(user, details, timeout);
    }

    public UserSession login(User user, Map<String, Object> details, long timeout) {
        checkNullValue(user);
        return getUserSession(user, details, timeout);
    }

    private void checkNullValue(User user) {
        if (null == user) {
            throw BusinessError.raise(Errors.AUTHENTICATION_FAILED)
                    .message("Failed to login.")
                    .build();
        }
    }

    private void verifyPassword(User user, String password) {
        if (!user.verifyPassword(password)) {
            throw BusinessError.raise(Errors.AUTHENTICATION_FAILED)
                    .message("Failed to login.")
                    .build();
        }
    }

    private UserSession getUserSession(User user, Map<String, Object> details, long timeout) {
        Application application = user.getApplication();
        return userSessionFactory.generate(application, user, details, timeout);
    }

}
