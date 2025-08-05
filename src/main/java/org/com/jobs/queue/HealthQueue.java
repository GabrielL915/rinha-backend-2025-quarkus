package org.com.jobs.queue;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@ApplicationScoped
public class HealthQueue {
    private final LinkedBlockingQueue<Supplier<Boolean>> healthChecksQueue = new LinkedBlockingQueue<>();
    private ScheduledExecutorService scheduler;

    public void enqueue(Supplier<Boolean> healthCheck) {
        healthChecksQueue.offer(healthCheck);
    }

    public Supplier<Boolean> dequeue() throws InterruptedException {
        return healthChecksQueue.take();
    }

    public void schedulePeriodic(Supplier<Boolean> healthCheck, long period, TimeUnit unit) {
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
        }
        scheduler.scheduleAtFixedRate(() -> enqueue(healthCheck), 0, period, unit);
    }
}

