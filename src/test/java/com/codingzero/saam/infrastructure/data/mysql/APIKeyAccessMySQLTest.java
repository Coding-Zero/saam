package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.spi.APIKeyAccessTest;
import com.codingzero.saam.infrastructure.data.APIKeyAccess;
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
        return Helper.sharedInstance().getMySQLAccessFactory().getAPIKeyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess().generateId();
    }

    @Override
    protected String getPrincipalId(String applicationId) {
        return Helper.sharedInstance().getMySQLAccessFactory().getPrincipalAccess().generateId(applicationId, PrincipalType.API_KEY);
    }

    @Override
    protected String getUserId(String applicationId) {
        return getPrincipalId(applicationId);
    }
}
