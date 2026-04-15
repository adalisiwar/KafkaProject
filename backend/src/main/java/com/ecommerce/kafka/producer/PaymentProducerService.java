package com.ecommerce.kafka.producer;

import com.ecommerce.kafka.config.KafkaTopicConfig;
import com.ecommerce.kafka.event.OrderPaidEvent;
import com.ecommerce.kafka.service.KafkaLoggingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducerService extends AbstractKafkaProducer {

    public PaymentProducerService(KafkaTemplate<String, Object> kafkaTemplate, KafkaLoggingService kafkaLoggingService) {
        super(kafkaTemplate, kafkaLoggingService);
    }

    public void publish(OrderPaidEvent event) {
        send(KafkaTopicConfig.ORDER_PAID, String.valueOf(event.getOrderId()), event);
    }
}
