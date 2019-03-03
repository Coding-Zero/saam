package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.infrastructure.data.spi.UsernamePolicyAccessTest;
import com.codingzero.saam.infrastructure.data.UsernamePolicyAccess;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class UsernamePolicyAccessMySQLTest extends UsernamePolicyAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected UsernamePolicyAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getUsernamePolicyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess().generateId();
    }

}
