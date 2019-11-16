package com.codingzero.saam.protocol.rest;

import com.codingzero.saam.protocol.rest.auth.APIKeyAuthHandler;
import com.codingzero.saam.protocol.rest.auth.AuthFailedExceptionMapper;
import com.codingzero.saam.protocol.rest.auth.AuthFilter;
import com.codingzero.saam.protocol.rest.auth.AuthHandlerManager;
import com.codingzero.saam.protocol.rest.health.SAAMHealthCheck;
import com.codingzero.saam.protocol.rest.resources.APIKeyResource;
import com.codingzero.saam.protocol.rest.resources.ApplicationResource;
import com.codingzero.saam.protocol.rest.resources.OtherResource;
import com.codingzero.saam.protocol.rest.resources.PermissionResource;
import com.codingzero.saam.protocol.rest.resources.ResourceResource;
import com.codingzero.saam.protocol.rest.resources.RoleResource;
import com.codingzero.saam.protocol.rest.resources.UserResource;
import com.codingzero.saam.protocol.rest.resources.UserSessionResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.sql.DataSource;
import java.util.Arrays;

public class SAAMApplication extends Application<SAAMConfiguration> {

    public static void main(final String[] args) throws Exception {
        new SAAMApplication().run(args);
    }

    @Override
    public String getName() {
        return "SAAM";
    }

    @Override
    public void initialize(final Bootstrap<SAAMConfiguration> bootstrap) {

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        new MixinRegister().register(bootstrap.getObjectMapper());
    }

    @Override
    public void run(final SAAMConfiguration configuration,
                    final Environment environment) {
        DataSource dataSource = new DataSourceProvider(configuration).get();
        CloseableHttpClient httpClient = new HttpClientProvider().get();
        SAAMSupplier saamSupplier = new SAAMSupplier(dataSource, httpClient);

        environment.jersey().register(new AuthFilter(new AuthHandlerManager(
                Arrays.asList(
                        new APIKeyAuthHandler(configuration.getApiKey())
                )
        )));

        //register exception mapper
        environment.jersey().register(new BusinessErrorMapper(environment.metrics()));
        environment.jersey().register(new AuthFailedExceptionMapper(environment.metrics()));

        //register resources
        environment.jersey().register(new ApplicationResource(saamSupplier, environment.getObjectMapper()));
        environment.jersey().register(new UserResource(saamSupplier, environment.getObjectMapper()));
        environment.jersey().register(new OtherResource(saamSupplier, environment.getObjectMapper()));
        environment.jersey().register(new APIKeyResource(saamSupplier, environment.getObjectMapper()));
        environment.jersey().register(new UserSessionResource(saamSupplier, environment.getObjectMapper()));
        environment.jersey().register(new RoleResource(saamSupplier, environment.getObjectMapper()));
        environment.jersey().register(new ResourceResource(saamSupplier, environment.getObjectMapper()));
        environment.jersey().register(new PermissionResource(saamSupplier, environment.getObjectMapper()));

        //register heath check
        final SAAMHealthCheck healthCheck = new SAAMHealthCheck();
        environment.healthChecks().register("healthCheck", healthCheck);

        //setup global url path
        environment.jersey().setUrlPattern("/api/v2/*");
    }

}
