package com.ecommerce.kafka.service;

import com.ecommerce.kafka.event.NotificationEmailEvent;
import com.ecommerce.kafka.model.Order;
import com.ecommerce.kafka.model.OrderItem;
import com.ecommerce.kafka.model.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendStatusEmail(Order order, NotificationEmailEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(order.getCustomer().getEmail());
            helper.setSubject(subject(event.getStatus(), order.getId()));
            helper.setText(buildHtml(order, event), true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send email for order " + order.getId(), ex);
        }
    }

    private String subject(OrderStatus status, Long orderId) {
        return switch (status) {
            case VALIDATED -> "Order #" + orderId + " confirmed";
            case REJECTED -> "Order #" + orderId + " rejected";
            case PAID -> "Payment received for order #" + orderId;
            case SHIPPED -> "Order #" + orderId + " shipped";
            default -> "Order update #" + orderId;
        };
    }

    private String buildHtml(Order order, NotificationEmailEvent event) {
        String rows = order.getItems().stream()
                .map(this::row)
                .collect(Collectors.joining());
        String extra = event.getStatus() == OrderStatus.SHIPPED
                ? "<p><strong>Tracking number:</strong> " + event.getTrackingNumber() + "</p>"
                : "";
        return """
                <html>
                  <body style="font-family:Arial,sans-serif;background:#f8fafc;padding:24px;">
                    <div style="max-width:720px;margin:auto;background:white;border-radius:16px;padding:24px;border:1px solid #e2e8f0;">
                      <h2 style="margin-top:0;color:#0f172a;">%s</h2>
                      <p>Hello %s,</p>
                      <p>%s</p>
                      %s
                      <table style="width:100%%;border-collapse:collapse;margin-top:16px;">
                        <thead>
                          <tr style="background:#e2e8f0;">
                            <th style="padding:10px;text-align:left;">Product</th>
                            <th style="padding:10px;text-align:left;">Qty</th>
                            <th style="padding:10px;text-align:left;">Unit Price</th>
                          </tr>
                        </thead>
                        <tbody>%s</tbody>
                      </table>
                      <p style="margin-top:16px;"><strong>Total:</strong> %s</p>
                    </div>
                  </body>
                </html>
                """.formatted(subject(event.getStatus(), order.getId()), order.getCustomer().getName(), event.getMessage(), extra, rows, order.getTotalAmount());
    }

    private String row(OrderItem item) {
        return """
                <tr>
                  <td style="padding:10px;border-bottom:1px solid #e2e8f0;">%s</td>
                  <td style="padding:10px;border-bottom:1px solid #e2e8f0;">%s</td>
                  <td style="padding:10px;border-bottom:1px solid #e2e8f0;">%s</td>
                </tr>
                """.formatted(item.getProduct().getName(), item.getQuantity(), item.getUnitPrice());
    }
}
