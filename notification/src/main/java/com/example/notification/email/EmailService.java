package com.example.notification.email;

import com.example.notification.kafka.order.Product;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendPaymentSuccessEmail(
        String destination,
        String customerName,
        BigDecimal amount,
        String orderReference
    ) throws MessagingException {
        var message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(
            message,
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
        );
        messageHelper.setFrom("contact@mehrab.com");

        final String templateName = EmailTemplates.PAYMENT_SUCCESSFUL.getTemplate();

        var variables = new HashMap<String, Object>();
        variables.put("customerName", customerName);
        variables.put("amount", amount);
        variables.put("orderReference", orderReference);

        var context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(EmailTemplates.PAYMENT_SUCCESSFUL.getSubject());

        try {
            var html = templateEngine.process(templateName, context);
            messageHelper.setText(html, true);

            messageHelper.setTo(destination);
            mailSender.send(message);
            log.info("Email sent to {} with template {}", destination, templateName);
        } catch (MessagingException e) {
            log.warn("Cannot send email to {}", destination);
        }
    }

    @Async
    public void sendOrderConfirmationEmail(
        String destination,
        String customerName,
        BigDecimal amount,
        String orderReference,
        List<Product> products
    ) throws MessagingException {
        var message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(
            message,
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
        );
        messageHelper.setFrom("contact@mehrab.com");

        final String templateName = EmailTemplates.ORDER_CONFIRMATION.getTemplate();

        var variables = new HashMap<String, Object>();
        variables.put("customerName", customerName);
        variables.put("totalAmount", amount);
        variables.put("orderReference", orderReference);
        variables.put("products", products);

        var context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(EmailTemplates.ORDER_CONFIRMATION.getSubject());

        try {
            var html = templateEngine.process(templateName, context);
            messageHelper.setText(html, true);

            messageHelper.setTo(destination);
            mailSender.send(message);
            log.info("Email sent to {} with template {}", destination, templateName);
        } catch (MessagingException e) {
            log.warn("Cannot send email to {}", destination);
        }
    }
}
