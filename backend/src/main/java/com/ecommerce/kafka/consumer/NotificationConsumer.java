package com.ecommerce.kafka.consumer;

import com.ecommerce.kafka.config.KafkaTopicConfig;
import com.ecommerce.kafka.event.NotificationEmailEvent;
import com.ecommerce.kafka.model.Order;
import com.ecommerce.kafka.service.EmailService;
import com.ecommerce.kafka.service.KafkaLoggingService;
import com.ecommerce.kafka.service.NotificationService;
import com.ecommerce.kafka.service.OrderService;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final EmailService emailService;
    private final NotificationService notificationService;
    private final OrderService orderService;
    private final KafkaLoggingService kafkaLoggingService;

    public NotificationConsumer(EmailService emailService,
                                NotificationService notificationService,
                                OrderService orderService,
                                KafkaLoggingService kafkaLoggingService) {
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.orderService = orderService;
        this.kafkaLoggingService = kafkaLoggingService;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopicConfig.NOTIFICATION_EMAIL, groupId = "notification-group", containerFactory = "notificationKafkaListenerContainerFactory")
    public void handle(NotificationEmailEvent event, ConsumerRecord<String, NotificationEmailEvent> record, Acknowledgment acknowledgment) {
        kafkaLoggingService.logConsumer("notification-group", record, event);
        Order order = orderService.loadDetailed(event.getOrderId());
        emailService.sendStatusEmail(order, event);
        notificationService.save(order, event.getNotificationType(), true);
        acknowledgment.acknowledge();
    }
}
