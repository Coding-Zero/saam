package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.infrastructure.data.spi.EmailPolicyAccessTest;
import com.codingzero.saam.infrastructure.data.EmailPolicyAccess;
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
        return Helper.sharedInstance().getMySQLAccessFactory().getEmailPolicyAccess();
    }

    @Override
    protected String getApplicationId() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess().generateId();
    }

}
