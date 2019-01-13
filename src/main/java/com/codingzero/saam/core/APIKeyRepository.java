package com.codingzero.saam.core;

import java.util.List;

public interface APIKeyRepository {

    APIKey store(APIKey apiKey);

    void remove(APIKey apiKey);

    void removeByOwner(User user);

    void removeByApplication(Application application);

    APIKey findById(Application application, String id);

    List<APIKey> findByOwner(User user);

}
