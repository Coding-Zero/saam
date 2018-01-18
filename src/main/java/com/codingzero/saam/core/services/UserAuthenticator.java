package com.codingzero.saam.core.services;

import com.codingzero.saam.common.Errors;
import com.codingzero.saam.core.Application;
import com.codingzero.saam.core.User;
import com.codingzero.saam.core.UserSession;
import com.codingzero.utilities.error.BusinessError;

import java.util.Map;

public class UserAuthenticator {

    public UserSession login(User user, String password, Map<String, Object> details, long timeout) {
        if (null == user || !user.verifyPassword(password)) {
            throw BusinessError.raise(Errors.AUTHENTICATION_FAILED)
                    .message("Invalid credential to login")
                    .build();
        }
        Application application = user.getApplication();
        return application.createUserSession(user, details, timeout);
    }

    public UserSession login(User user, Map<String, Object> details, long timeout) {
        if (null == user) {
            throw BusinessError.raise(Errors.AUTHENTICATION_FAILED)
                    .message("Failed to login with OAuth.")
                    .build();
        }
        Application application = user.getApplication();
        return application.createUserSession(user, details, timeout);
    }

}
