package com.codingzero.saam.presentation;

import com.codingzero.saam.presentation.health.SAAMHealthCheck;
import com.codingzero.saam.presentation.resources.APIKeyResource;
import com.codingzero.saam.presentation.resources.ApplicationResource;
import com.codingzero.saam.presentation.resources.OtherResource;
import com.codingzero.saam.presentation.resources.PermissionResource;
import com.codingzero.saam.presentation.resources.ResourceResource;
import com.codingzero.saam.presentation.resources.RoleResource;
import com.codingzero.saam.presentation.resources.UserResource;
import com.codingzero.saam.presentation.resources.UserSessionResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.sql.DataSource;

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

        //register exception mapper
        environment.jersey().register(new BusinessErrorMapper(environment.metrics()));

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
        environment.jersey().setUrlPattern("/api/v1/*");
    }

}
