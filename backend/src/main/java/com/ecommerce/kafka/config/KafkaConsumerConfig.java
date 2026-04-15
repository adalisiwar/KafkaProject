package com.ecommerce.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private final String bootstrapServers;

    public KafkaConsumerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @Bean
    CommonErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(KafkaTopicConfig.ORDER_DLQ, record.partition())
        );
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, com.ecommerce.kafka.event.OrderCreatedEvent> orderCreatedKafkaListenerContainerFactory(CommonErrorHandler errorHandler) {
        return buildFactory("inventory-group", com.ecommerce.kafka.event.OrderCreatedEvent.class, errorHandler);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, com.ecommerce.kafka.event.OrderValidatedEvent> orderValidatedKafkaListenerContainerFactory(CommonErrorHandler errorHandler) {
        return buildFactory("order-status-group", com.ecommerce.kafka.event.OrderValidatedEvent.class, errorHandler);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, com.ecommerce.kafka.event.OrderRejectedEvent> orderRejectedKafkaListenerContainerFactory(CommonErrorHandler errorHandler) {
        return buildFactory("order-status-group", com.ecommerce.kafka.event.OrderRejectedEvent.class, errorHandler);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, com.ecommerce.kafka.event.OrderPaidEvent> orderPaidKafkaListenerContainerFactory(CommonErrorHandler errorHandler) {
        return buildFactory("order-status-group", com.ecommerce.kafka.event.OrderPaidEvent.class, errorHandler);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, com.ecommerce.kafka.event.OrderShippedEvent> orderShippedKafkaListenerContainerFactory(CommonErrorHandler errorHandler) {
        return buildFactory("order-status-group", com.ecommerce.kafka.event.OrderShippedEvent.class, errorHandler);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, com.ecommerce.kafka.event.NotificationEmailEvent> notificationKafkaListenerContainerFactory(CommonErrorHandler errorHandler) {
        return buildFactory("notification-group", com.ecommerce.kafka.event.NotificationEmailEvent.class, errorHandler);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, com.ecommerce.kafka.event.OrderPaidEvent> analyticsKafkaListenerContainerFactory(CommonErrorHandler errorHandler) {
        return buildFactory("analytics-group", com.ecommerce.kafka.event.OrderPaidEvent.class, errorHandler);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildFactory(String groupId, Class<T> eventClass, CommonErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(groupId, eventClass));
        factory.setCommonErrorHandler(errorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    private <T> ConsumerFactory<String, T> consumerFactory(String groupId, Class<T> eventClass) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        JsonDeserializer<T> valueDeserializer = new JsonDeserializer<>(eventClass);
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }
}
