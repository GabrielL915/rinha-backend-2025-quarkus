package org.com.jobs;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.domain.model.HealthResponse;
import org.com.domain.services.ProcessorService;

@ApplicationScoped
public class HealthMonitor {

    private volatile HealthResponse defaultHealth = new HealthResponse(true, -1);
    private volatile HealthResponse fallbackHealth = new HealthResponse(true, -1);

    @Inject
    ProcessorService processorService;

    @Scheduled(every = "5s")
    void refreshHealthStatuses() {
        try {
            defaultHealth = processorService.fetchCheckHealth(true);
        } catch (Exception e) {
            defaultHealth = new HealthResponse(true, -1);
        }
        try {
            fallbackHealth = processorService.fetchCheckHealth(false);
        } catch (Exception e) {
            fallbackHealth = new HealthResponse(true, -1);
        }
    }

    public HealthResponse getHealth(boolean isDefault) {
        return isDefault ? defaultHealth : fallbackHealth;
    }
}