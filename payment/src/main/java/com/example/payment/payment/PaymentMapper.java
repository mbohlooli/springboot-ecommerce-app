package com.example.payment.payment;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public Payment toPayment(@Valid PaymentRequest request) {
        return Payment.builder()
            .id(request.id())
            .orderId(request.orderId())
            .paymentMethod(request.paymentMethod())
            .amount(request.amount())
            .build();
    }
}
