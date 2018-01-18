package com.codingzero.saam.core;

public interface ApplicationFactory {

    Application generate(String name, String description);
    
}
