package com.ecommerce.kafka.service;

import com.ecommerce.kafka.event.KafkaOrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class KafkaLoggingService {

    private static final Logger log = LoggerFactory.getLogger(KafkaLoggingService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

    public void logProducer(String topic, String key, KafkaOrderEvent event, RecordMetadata metadata) {
        long latency = event.getOccurredAt() == null ? 0 : Duration.between(event.getOccurredAt(), Instant.now()).toMillis();
        log.info("[{}] [SENT]     | {} | {} | {} | P:{} O:{} latency:{}ms",
                FORMATTER.format(Instant.now()),
                topic,
                key,
                event.summary(),
                metadata.partition(),
                metadata.offset(),
                latency);
    }

    public void logConsumer(String group, ConsumerRecord<String, ?> record, KafkaOrderEvent event) {
        long lag = event.getOccurredAt() == null ? 0 : Duration.between(event.getOccurredAt(), Instant.now()).toMillis();
        log.info("[{}] [RECEIVED] | {} | {} | {} | {} | P:{} O:{} lag:{}ms",
                FORMATTER.format(Instant.now()),
                record.topic(),
                group,
                record.key(),
                event.summary(),
                record.partition(),
                record.offset(),
                lag);
    }
}
