package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.UserAccess;
import com.codingzero.saam.infrastructure.database.spi.UserAccessTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class UserAccessMySQLTest extends UserAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected UserAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessHelper().getUserAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessHelper().getApplicationAccess().generateId();
    }

    @Override
    protected String getPrincipalId(String applicationId, PrincipalType type) {
        return Helper.sharedInstance().getMySQLAccessHelper().getPrincipalAccess().generateId(applicationId, type);
    }
}
