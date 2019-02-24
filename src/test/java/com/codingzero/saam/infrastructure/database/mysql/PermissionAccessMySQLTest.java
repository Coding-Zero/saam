package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.database.PermissionAccess;
import com.codingzero.saam.infrastructure.database.spi.PermissionAccessTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class PermissionAccessMySQLTest extends PermissionAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected PermissionAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessHelper().getPermissionAccess();
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
