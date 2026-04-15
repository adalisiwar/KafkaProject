package com.ecommerce.kafka.event;

import java.time.Instant;

public interface KafkaOrderEvent {
    Long getOrderId();
    Instant getOccurredAt();
    String summary();
}
