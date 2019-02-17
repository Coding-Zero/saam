package com.codingzero.saam.domain;

public interface ApplicationFactory {

    Application generate(String name, String description);
    
}
