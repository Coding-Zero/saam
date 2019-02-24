package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.RoleAccess;
import com.codingzero.saam.infrastructure.database.spi.RoleAccessTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class RoleAccessMySQLTest extends RoleAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected RoleAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessHelper().getRoleAccess();
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
