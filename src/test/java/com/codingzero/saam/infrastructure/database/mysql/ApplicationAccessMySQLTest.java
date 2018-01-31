package com.codingzero.saam.infrastructure.database.mysql;

import com.codingzero.saam.infrastructure.database.spi.ApplicationAccessTest;
import com.codingzero.saam.infrastructure.database.spi.ApplicationAccess;
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
        return Helper.sharedInstance().getMySQLAccessModule().getApplicationAccess();
    }
}
