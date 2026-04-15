package com.ecommerce.kafka.producer;

import com.ecommerce.kafka.config.KafkaTopicConfig;
import com.ecommerce.kafka.event.NotificationEmailEvent;
import com.ecommerce.kafka.event.OrderRejectedEvent;
import com.ecommerce.kafka.event.OrderValidatedEvent;
import com.ecommerce.kafka.service.KafkaLoggingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StatusProducerService extends AbstractKafkaProducer {

    public StatusProducerService(KafkaTemplate<String, Object> kafkaTemplate, KafkaLoggingService kafkaLoggingService) {
        super(kafkaTemplate, kafkaLoggingService);
    }

    public void publishValidated(OrderValidatedEvent event) {
        send(KafkaTopicConfig.ORDER_VALIDATED, String.valueOf(event.getOrderId()), event);
    }

    public void publishRejected(OrderRejectedEvent event) {
        send(KafkaTopicConfig.ORDER_REJECTED, String.valueOf(event.getOrderId()), event);
    }

    public void publishNotification(NotificationEmailEvent event) {
        send(KafkaTopicConfig.NOTIFICATION_EMAIL, String.valueOf(event.getOrderId()), event);
    }
}
