package com.example.notification.kafka;

import com.example.notification.email.EmailService;
import com.example.notification.kafka.order.OrderConfirmation;
import com.example.notification.kafka.payment.PaymentNotificationRequest;
import com.example.notification.notification.Notification;
import com.example.notification.notification.NotificationRepository;
import com.example.notification.notification.NotificationType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationRepository repository;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotification(PaymentNotificationRequest paymentNotificationRequest) throws MessagingException {
        log.info("Received Payment Confirmation: {}", paymentNotificationRequest);

        repository.save(
            Notification.builder()
                .type(NotificationType.PAYMENT_CONFIRMATION)
                .notificationDate(LocalDateTime.now())
                .paymentNotificationRequest(paymentNotificationRequest)
                .build()
        );

        var customerName = paymentNotificationRequest.customerFirstName() + " " + paymentNotificationRequest.customerLastName();
        emailService.sendPaymentSuccessEmail(
            paymentNotificationRequest.customerEmail(),
            customerName,
            paymentNotificationRequest.amount(),
            paymentNotificationRequest.orderReference()
        );
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotification(OrderConfirmation orderConfirmation) throws MessagingException {
        log.info("Received Order Confirmation: {}", orderConfirmation);

        repository.save(
            Notification.builder()
                .type(NotificationType.ORDER_CONFIRMATION)
                .notificationDate(LocalDateTime.now())
                .orderConfirmation(orderConfirmation)
                .build()
        );

        var customerName = orderConfirmation.customer().firstName() + " " + orderConfirmation.customer().lastName();
        emailService.sendOrderConfirmationEmail(
            orderConfirmation.customer().email(),
            customerName,
            orderConfirmation.totalAmount(),
            orderConfirmation.orderReference(),
            orderConfirmation.products()
        );
    }
}
