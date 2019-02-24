package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.infrastructure.database.spi.EmailPolicyAccessTest;
import com.codingzero.saam.infrastructure.database.EmailPolicyAccess;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class EmailPolicyAccessMySQLTest extends EmailPolicyAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected EmailPolicyAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessHelper().getEmailPolicyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessHelper().getApplicationAccess().generateId();
    }

}
