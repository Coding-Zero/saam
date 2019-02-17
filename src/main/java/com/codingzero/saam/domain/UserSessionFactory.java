package com.codingzero.saam.domain;

import java.util.Map;

public interface UserSessionFactory {

    UserSession generate(Application application,
                         User user,
                         Map<String, Object> details,
                         long timeout);
}
