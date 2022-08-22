package io.quarkus.workshop.superheroes.villain.health;

import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import io.quarkus.workshop.superheroes.villain.VillainResource;

@Liveness
public class PingVillainResourceHealthCheck implements HealthCheck {
    @Inject VillainResource villainResource;

    @Override
    public HealthCheckResponse call() {
        String response = villainResource.hello();
        return HealthCheckResponse.named("Ping Villain Rest Endpoint").withData("Response", response).up().build();
    }
    

}
