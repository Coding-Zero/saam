package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.UserSessionAccess;
import com.codingzero.saam.infrastructure.database.spi.UserSessionAccessTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class UserSessionAccessMySQLTest extends UserSessionAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected UserSessionAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessHelper().getUserSessionAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessHelper().getApplicationAccess().generateId();
    }

    @Override
    protected String getKey(String applicationId) {
        return Helper.sharedInstance().getMySQLAccessHelper().getUserSessionAccess().generateKey(applicationId);
    }

    @Override
    protected String getPrincipalId(String applicationId, PrincipalType type) {
        return Helper.sharedInstance().getMySQLAccessHelper().getPrincipalAccess().generateId(applicationId, type);
    }
}
