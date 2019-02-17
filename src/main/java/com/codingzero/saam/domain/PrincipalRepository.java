package com.codingzero.saam.domain;

public interface PrincipalRepository {

    Principal findById(Application application, String id);

}
