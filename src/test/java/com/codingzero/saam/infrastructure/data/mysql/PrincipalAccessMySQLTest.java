package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.PrincipalAccess;
import com.codingzero.saam.infrastructure.data.spi.PrincipalAccessTest;
import com.codingzero.saam.infrastructure.data.RoleAccess;
import com.codingzero.saam.infrastructure.data.UserAccess;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class PrincipalAccessMySQLTest extends PrincipalAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected PrincipalAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getPrincipalAccess();
    }

    @Override
    protected UserAccess getUserAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getUserAccess();
    }

    @Override
    protected RoleAccess getRoleAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getRoleAccess();
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
