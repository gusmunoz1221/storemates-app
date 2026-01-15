package com.storemates.notification;

import com.storemates.order.entity.OrderEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendOrderConfirmation(OrderEntity order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getCustomerEmail());
            helper.setSubject("¡Pago Exitoso! Tu orden #" + order.getId() + " esta confirmada 🧉");

            Context context = new Context();
            // metemos la orden completa dentro. En el html se llama order
            context.setVariable("order", order);

            // procesamos el archivo order-confirmation.html
            String htmlContent = templateEngine.process("order-confirmation", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info(">>> HTML MAIL ENVIADO a: {}", order.getCustomerEmail());

        } catch (MessagingException e) {
            log.error("Error! no se pudo enviar  mail", e);
        }
    }
}