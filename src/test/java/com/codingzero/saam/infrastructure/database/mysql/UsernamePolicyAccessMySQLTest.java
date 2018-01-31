package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.infrastructure.database.spi.UsernamePolicyAccessTest;
import com.codingzero.saam.infrastructure.database.spi.UsernamePolicyAccess;
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
        return Helper.sharedInstance().getMySQLAccessModule().getUsernamePolicyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessModule().getApplicationAccess().generateId();
    }

}
