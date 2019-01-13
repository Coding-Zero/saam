package com.codingzero.saam.core;

public interface PrincipalRepository {

    Principal findById(Application application, String id);

}
