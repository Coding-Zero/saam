package com.codingzero.saam.infrastructure.mysql;

import com.codingzero.saam.app.server.base.mysql.MySQLAccessModule;
import com.codingzero.saam.presentation.DataSourceProvider;
import com.codingzero.saam.presentation.SAAMApplication;
import com.codingzero.saam.presentation.SAAMConfiguration;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;

public class Helper {

    public static final DropwizardTestSupport<SAAMConfiguration> SUPPORT = new DropwizardTestSupport<>(
            SAAMApplication.class,
            ResourceHelpers.resourceFilePath("config-int.yml"));

    private static MySQLAccessModule mySQLAccessModule;

    public static MySQLAccessModule getMySQLAccessModule() {
        if (null == mySQLAccessModule) {
            DataSourceProvider provider = new DataSourceProvider(SUPPORT.getConfiguration());
            mySQLAccessModule = new MySQLAccessModule(provider.get());
        }
        return mySQLAccessModule;
    }
}
