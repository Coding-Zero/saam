package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.common.PrincipalType;
import com.codingzero.saam.infrastructure.data.UserSessionAccess;
import com.codingzero.saam.infrastructure.data.spi.UserSessionAccessTest;
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
        return Helper.sharedInstance().getMySQLAccessFactory().getUserSessionAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess().generateId();
    }

    @Override
    protected String getKey(String applicationId) {
        return Helper.sharedInstance().getMySQLAccessFactory().getUserSessionAccess().generateKey(applicationId);
    }

    @Override
    protected String getPrincipalId(String applicationId, PrincipalType type) {
        return Helper.sharedInstance().getMySQLAccessFactory().getPrincipalAccess().generateId(applicationId, type);
    }
}
