package org.com.jobs.worker;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.domain.model.HealthResponse;
import org.com.domain.model.Payment;
import org.com.domain.services.ProcessorService;
import org.com.jobs.HealthMonitor;
import org.com.jobs.queue.PaymentQueue;
import org.com.repository.PaymentRepository;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class PaymentWorker {

    @Inject
    PaymentQueue paymentQueue;

    @Inject
    ProcessorService processorService;

    @Inject
    HealthMonitor healthMonitor;

    @Inject
    PaymentRepository paymentRepository;

    private final ExecutorService executor =
            Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

    @Scheduled(every = "1s")
    void pollQueue() throws InterruptedException {
        Payment payment;
        while ((payment = paymentQueue.dequeue()) != null) {
            Payment finalPayment = payment;
            executor.submit(() -> handlePayment(finalPayment));
        }
    }

    private void handlePayment(Payment payment) {
        try {
            HealthResponse dh = healthMonitor.getHealth(payment.isDefault());
            boolean useDefault = !dh.isFailing();

            if (!useDefault) {
                HealthResponse fh = healthMonitor.getHealth(payment.isDefault());
                useDefault = !fh.isFailing();
            }

            payment.setDefault(useDefault);
            payment.setRequestAt(Instant.now());

            String result = processorService.fetchPaymentProcessor(
                    payment.getCorrelationId().toString(),
                    payment.getAmount(),
                    payment.getRequestAt().toString(),
                    payment.isDefault()
            );

            if ("OK".equals(result)) {
                paymentRepository.insertPayment(payment);
            } else {
                throw new RuntimeException("Resposta inesperada: " + result);
            }

        } catch (Exception e) {
            paymentQueue.enqueue(payment);
        }
    }
}