package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.protocol.rest.SAAMConfiguration;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class SAAMTestApplication extends Application<SAAMConfiguration> {

    @Override
    public String getName() {
        return "SAAMTest";
    }

    @Override
    public void initialize(final Bootstrap<SAAMConfiguration> bootstrap) {

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(final SAAMConfiguration configuration,
                    final Environment environment) {

    }

}
