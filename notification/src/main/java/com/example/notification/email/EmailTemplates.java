package com.example.notification.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplates {
    PAYMENT_SUCCESSFUL("payment-successful.html", "Payment successfully processed."),
    ORDER_CONFIRMATION("order-confirmation.html", "Order successfully confirmed.");

    private final String template;
    private final String subject;
}
