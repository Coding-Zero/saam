package com.codingzero.saam.core;

import java.util.Map;

public interface UserSessionFactory {

    UserSession generate(Application application,
                         User user,
                         Map<String, Object> details,
                         long timeout);
}
