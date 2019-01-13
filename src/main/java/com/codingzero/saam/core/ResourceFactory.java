package com.codingzero.saam.core;

public interface ResourceFactory {

    Resource generate(Application application, String key, Principal owner);

}
