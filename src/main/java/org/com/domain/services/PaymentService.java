package org.com.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.com.domain.model.Payment;
import org.com.repository.PaymentRepository;

import java.io.IOException;

@ApplicationScoped
public class PaymentService {

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    ProcessorService processorService;

    public String processPayment(Payment payment) throws IOException, InterruptedException {

        //recebi payment do worker ja checado health check

        //e melhor verificar aqui para qual processor

        //salvar dados

        String result = processorService.fetchPaymentProcessor(
                payment.getCorrelationId().toString(),
                payment.getAmount(),
                payment.getRequestAt().toString(),
                payment.isDefault());

        paymentRepository.insertPayment(payment);

        return "OK";
    }
}
