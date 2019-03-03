package com.codingzero.saam.infrastructure.data.mysql;

import com.codingzero.saam.infrastructure.data.spi.ApplicationAccessTest;
import com.codingzero.saam.infrastructure.data.ApplicationAccess;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ApplicationAccessMySQLTest extends ApplicationAccessTest {

    @BeforeClass
    public static void beforeClass() {
        Helper.sharedInstance().init();
    }

    @AfterClass
    public static void afterClass() {
        Helper.sharedInstance().clean();
    }

    @Override
    protected ApplicationAccess getAccess() {
        return Helper.sharedInstance().getMySQLAccessFactory().getApplicationAccess();
    }
}
