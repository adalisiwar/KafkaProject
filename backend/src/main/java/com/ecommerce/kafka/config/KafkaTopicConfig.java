package com.ecommerce.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_VALIDATED = "order.validated";
    public static final String ORDER_REJECTED = "order.rejected";
    public static final String ORDER_PAID = "order.paid";
    public static final String ORDER_SHIPPED = "order.shipped";
    public static final String NOTIFICATION_EMAIL = "notification.email";
    public static final String ORDER_DLQ = "order.dlq";

    @Bean
    NewTopic orderCreatedTopic() {
        return build(ORDER_CREATED);
    }

    @Bean
    NewTopic orderValidatedTopic() {
        return build(ORDER_VALIDATED);
    }

    @Bean
    NewTopic orderRejectedTopic() {
        return build(ORDER_REJECTED);
    }

    @Bean
    NewTopic orderPaidTopic() {
        return build(ORDER_PAID);
    }

    @Bean
    NewTopic orderShippedTopic() {
        return build(ORDER_SHIPPED);
    }

    @Bean
    NewTopic notificationEmailTopic() {
        return build(NOTIFICATION_EMAIL);
    }

    @Bean
    NewTopic orderDlqTopic() {
        return build(ORDER_DLQ);
    }

    private NewTopic build(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
