package com.ecommerce.kafka.producer;

import com.ecommerce.kafka.config.KafkaTopicConfig;
import com.ecommerce.kafka.event.OrderCreatedEvent;
import com.ecommerce.kafka.service.KafkaLoggingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService extends AbstractKafkaProducer {

    public OrderProducerService(KafkaTemplate<String, Object> kafkaTemplate, KafkaLoggingService kafkaLoggingService) {
        super(kafkaTemplate, kafkaLoggingService);
    }

    public void publish(OrderCreatedEvent event) {
        send(KafkaTopicConfig.ORDER_CREATED, String.valueOf(event.getOrderId()), event);
    }
}
