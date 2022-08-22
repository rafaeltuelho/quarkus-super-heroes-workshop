package io.quarkus.workshop.superheroes.hero.health;

import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import io.quarkus.workshop.superheroes.hero.HeroResource;

@Liveness
public class PingHeroResourceHealthCheck implements HealthCheck {

    @Inject
    HeroResource heroResource;

    @Override
    public HealthCheckResponse call() {
        String response = heroResource.hello();
        return HealthCheckResponse.named("Ping Hero REST Endpoint").withData("Response", response).up().build();
    }
}