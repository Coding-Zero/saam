package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.PermissionAccess;
import com.codingzero.saam.infrastructure.data.spi.PermissionAccessTest;
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
        return Helper.sharedInstance().getMySQLAccessFactory().getPermissionAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess().generateId();
    }

    @Override
    protected String getPrincipalId(String applicationId, PrincipalType type) {
        return Helper.sharedInstance().getMySQLAccessFactory().getPrincipalAccess().generateId(applicationId, type);
    }
}
