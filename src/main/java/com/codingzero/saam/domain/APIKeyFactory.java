package com.codingzero.saam.domain;

public interface APIKeyFactory {

    APIKey generate(Application application, User user, String name);

}
