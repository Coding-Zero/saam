package com.codingzero.saam.protocol.rest.health;

import com.codahale.metrics.health.HealthCheck;

public class SAAMHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
