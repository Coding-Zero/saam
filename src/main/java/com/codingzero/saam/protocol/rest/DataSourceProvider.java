package com.codingzero.saam.protocol.rest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceProvider {

    private SAAMConfiguration configuration;

    public DataSourceProvider(SAAMConfiguration configuration) {
        this.configuration = configuration;
    }

    public DataSource get() {
        String url = (String) configuration.getMysql().get("url");
        String username = (String) configuration.getMysql().get("username");
        String password = (String) configuration.getMysql().get("password");
        int connectionMaxPoolSize = (int) configuration.getMysql().get("connectionMaxPoolSize");
        int connectionMinPoolSize = (int) configuration.getMysql().get("connectionMinPoolSize");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + url + "?useUnicode=yes&characterEncoding=UTF-8");
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useSSL", "false");
        config.setMaximumPoolSize(connectionMaxPoolSize);
        config.setMinimumIdle(connectionMinPoolSize);
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(15000);
        config.setConnectionTimeout(30000); //30 seconds
        try {
            return new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
