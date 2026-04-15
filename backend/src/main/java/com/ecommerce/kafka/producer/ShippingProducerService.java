package com.ecommerce.kafka.producer;

import com.ecommerce.kafka.config.KafkaTopicConfig;
import com.ecommerce.kafka.event.OrderShippedEvent;
import com.ecommerce.kafka.service.KafkaLoggingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShippingProducerService extends AbstractKafkaProducer {

    public ShippingProducerService(KafkaTemplate<String, Object> kafkaTemplate, KafkaLoggingService kafkaLoggingService) {
        super(kafkaTemplate, kafkaLoggingService);
    }

    public void publish(OrderShippedEvent event) {
        send(KafkaTopicConfig.ORDER_SHIPPED, String.valueOf(event.getOrderId()), event);
    }
}
