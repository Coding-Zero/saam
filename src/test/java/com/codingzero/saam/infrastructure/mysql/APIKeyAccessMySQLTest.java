package com.codingzero.saam.infrastructure.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.APIKeyAccessTest;
import com.codingzero.saam.infrastructure.database.spi.APIKeyAccess;
import com.codingzero.saam.infrastructure.database.spi.PrincipalAccess;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static com.codingzero.saam.infrastructure.mysql.Helper.getMySQLAccessModule;

public class APIKeyAccessMySQLTest extends APIKeyAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.SUPPORT.before();
    }

    @AfterClass
    public static void afterClass() {
        Helper.SUPPORT.after();
    }

    @Override
    protected APIKeyAccess getAccess() {
        return getMySQLAccessModule().getAPIKeyAccess();
    }

    @Override
    protected PrincipalAccess getPrincipalAccess() {
        return getMySQLAccessModule().getPrincipalAccess();
    }

    @Override
    protected String getApplicationId() {
        return getMySQLAccessModule().getApplicationAccess().generateId();
    }

    @Override
    protected String getPrincipalId(String applicationId) {
        return getMySQLAccessModule().getPrincipalAccess().generateId(applicationId, PrincipalType.API_KEY);
    }

    @Override
    protected String getUserId(String applicationId) {
        return getPrincipalId(applicationId);
    }
}
