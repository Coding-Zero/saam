package com.codingzero.saam.core;

import com.codingzero.utilities.pagination.PaginatedResult;

import java.util.List;

public interface ApplicationRepository {

    Application store(Application application);
    
    void remove(Application application);
    
    Application findById(String id);

    PaginatedResult<List<Application>> findAll();
    
}
