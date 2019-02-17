package com.codingzero.saam.domain;

public interface ResourceFactory {

    Resource generate(Application application, String key, Principal owner);

}
