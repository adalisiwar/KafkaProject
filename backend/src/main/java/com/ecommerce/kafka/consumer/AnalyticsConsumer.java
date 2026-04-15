package com.ecommerce.kafka.consumer;

import com.ecommerce.kafka.config.KafkaTopicConfig;
import com.ecommerce.kafka.event.OrderPaidEvent;
import com.ecommerce.kafka.service.AnalyticsService;
import com.ecommerce.kafka.service.KafkaLoggingService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class AnalyticsConsumer {

    private final AnalyticsService analyticsService;
    private final KafkaLoggingService kafkaLoggingService;

    public AnalyticsConsumer(AnalyticsService analyticsService, KafkaLoggingService kafkaLoggingService) {
        this.analyticsService = analyticsService;
        this.kafkaLoggingService = kafkaLoggingService;
    }

    @KafkaListener(topics = KafkaTopicConfig.ORDER_PAID, groupId = "analytics-group", containerFactory = "analyticsKafkaListenerContainerFactory")
    public void handle(OrderPaidEvent event, ConsumerRecord<String, OrderPaidEvent> record, Acknowledgment acknowledgment) {
        kafkaLoggingService.logConsumer("analytics-group", record, event);
        LocalDate date = LocalDate.ofInstant(event.getOccurredAt(), ZoneId.systemDefault());
        analyticsService.recordPaidOrder(date, event.getTotalAmount());
        acknowledgment.acknowledge();
    }
}
