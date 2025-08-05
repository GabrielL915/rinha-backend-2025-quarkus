package org.com.jobs.queue;

import jakarta.enterprise.context.ApplicationScoped;
import org.com.domain.model.Payment;

import java.util.concurrent.LinkedBlockingQueue;

@ApplicationScoped
public class PaymentQueue {

    private final LinkedBlockingQueue<Payment> paymentsQueue = new LinkedBlockingQueue<>();

    public String enqueue(Payment payment) {
        paymentsQueue.offer(payment);
        return "OK";
    }

    public Payment dequeue() throws InterruptedException {
        return paymentsQueue.take();
    }
}
