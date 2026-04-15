package com.ecommerce.kafka.producer;

import com.ecommerce.kafka.event.KafkaOrderEvent;
import com.ecommerce.kafka.service.KafkaLoggingService;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaLoggingService kafkaLoggingService;

    protected AbstractKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate, KafkaLoggingService kafkaLoggingService) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaLoggingService = kafkaLoggingService;
    }

    protected void send(String topic, String key, KafkaOrderEvent event) {
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        throw new IllegalStateException("Kafka publish failed for topic " + topic, ex);
                    }
                    kafkaLoggingService.logProducer(topic, key, event, result.getRecordMetadata());
                });
    }
}
