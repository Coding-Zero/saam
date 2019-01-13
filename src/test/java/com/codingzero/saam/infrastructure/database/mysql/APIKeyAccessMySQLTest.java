package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.spi.APIKeyAccessTest;
import com.codingzero.saam.infrastructure.database.APIKeyAccess;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class APIKeyAccessMySQLTest extends APIKeyAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected APIKeyAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessModule().getAPIKeyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessModule().getApplicationAccess().generateId();
    }

    @Override
    protected String getPrincipalId(String applicationId) {
        return Helper.sharedInstance().getMySQLAccessModule().getPrincipalAccess().generateId(applicationId, PrincipalType.API_KEY);
    }

    @Override
    protected String getUserId(String applicationId) {
        return getPrincipalId(applicationId);
    }
}
