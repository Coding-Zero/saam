package com.codingzero.saam.infrastructure.database.spi;

import com.codingzero.saam.infrastructure.database.APIKeyOS;
import com.codingzero.saam.infrastructure.database.PrincipalOS;
import com.codingzero.utilities.pagination.PaginatedResult;
import com.codingzero.utilities.transaction.TransactionalService;

import java.util.List;

public interface APIKeyAccess extends TransactionalService {

    String generateSecretKey();

    void insert(APIKeyOS os);

    void update(APIKeyOS os);

    void delete(APIKeyOS os);

    void deleteByUserId(String applicationId, String userId);

    void deleteByApplicationId(String id);

    APIKeyOS selectByPrincipalOS(PrincipalOS os);

    List<APIKeyOS> selectByUserId(String applicationId, String userId);

    PaginatedResult<List<APIKeyOS>> selectByApplicationId(String applicationId);

}
