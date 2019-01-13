package com.codingzero.saam.core;

public interface APIKeyFactory {

    APIKey generate(Application application, User user, String name);

}
