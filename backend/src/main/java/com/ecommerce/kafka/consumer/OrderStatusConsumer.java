package com.ecommerce.kafka.consumer;

import com.ecommerce.kafka.config.KafkaTopicConfig;
import com.ecommerce.kafka.event.OrderPaidEvent;
import com.ecommerce.kafka.event.OrderRejectedEvent;
import com.ecommerce.kafka.event.OrderShippedEvent;
import com.ecommerce.kafka.event.OrderValidatedEvent;
import com.ecommerce.kafka.model.Order;
import com.ecommerce.kafka.model.OrderStatus;
import com.ecommerce.kafka.producer.ShippingProducerService;
import com.ecommerce.kafka.producer.StatusProducerService;
import com.ecommerce.kafka.service.EventMapper;
import com.ecommerce.kafka.service.KafkaLoggingService;
import com.ecommerce.kafka.service.OrderService;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderStatusConsumer {

    private final OrderService orderService;
    private final EventMapper eventMapper;
    private final StatusProducerService statusProducerService;
    private final ShippingProducerService shippingProducerService;
    private final KafkaLoggingService kafkaLoggingService;

    public OrderStatusConsumer(OrderService orderService,
                               EventMapper eventMapper,
                               StatusProducerService statusProducerService,
                               ShippingProducerService shippingProducerService,
                               KafkaLoggingService kafkaLoggingService) {
        this.orderService = orderService;
        this.eventMapper = eventMapper;
        this.statusProducerService = statusProducerService;
        this.shippingProducerService = shippingProducerService;
        this.kafkaLoggingService = kafkaLoggingService;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopicConfig.ORDER_VALIDATED, groupId = "order-status-group", containerFactory = "orderValidatedKafkaListenerContainerFactory")
    public void handleValidated(OrderValidatedEvent event, ConsumerRecord<String, OrderValidatedEvent> record, Acknowledgment acknowledgment) {
        kafkaLoggingService.logConsumer("order-status-group", record, event);
        Order order = orderService.updateStatus(event.getOrderId(), OrderStatus.VALIDATED);
        statusProducerService.publishNotification(eventMapper.toNotificationEvent(order, OrderStatus.VALIDATED, "Your order has been confirmed and inventory is reserved.", null));
        acknowledgment.acknowledge();
    }

    @Transactional
    @KafkaListener(topics = KafkaTopicConfig.ORDER_REJECTED, groupId = "order-status-group", containerFactory = "orderRejectedKafkaListenerContainerFactory")
    public void handleRejected(OrderRejectedEvent event, ConsumerRecord<String, OrderRejectedEvent> record, Acknowledgment acknowledgment) {
        kafkaLoggingService.logConsumer("order-status-group", record, event);
        Order order = orderService.updateStatus(event.getOrderId(), OrderStatus.REJECTED);
        statusProducerService.publishNotification(eventMapper.toNotificationEvent(order, OrderStatus.REJECTED, event.getReason(), null));
        acknowledgment.acknowledge();
    }

    @Transactional
    @KafkaListener(topics = KafkaTopicConfig.ORDER_PAID, groupId = "order-status-group", containerFactory = "orderPaidKafkaListenerContainerFactory")
    public void handlePaid(OrderPaidEvent event, ConsumerRecord<String, OrderPaidEvent> record, Acknowledgment acknowledgment) {
        kafkaLoggingService.logConsumer("order-status-group", record, event);
        Order order = orderService.updateStatus(event.getOrderId(), OrderStatus.PAID);
        statusProducerService.publishNotification(eventMapper.toNotificationEvent(order, OrderStatus.PAID, "Payment completed successfully.", null));
        String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        shippingProducerService.publish(eventMapper.toShippedEvent(order, trackingNumber));
        acknowledgment.acknowledge();
    }

    @Transactional
    @KafkaListener(topics = KafkaTopicConfig.ORDER_SHIPPED, groupId = "order-status-group", containerFactory = "orderShippedKafkaListenerContainerFactory")
    public void handleShipped(OrderShippedEvent event, ConsumerRecord<String, OrderShippedEvent> record, Acknowledgment acknowledgment) {
        kafkaLoggingService.logConsumer("order-status-group", record, event);
        Order order = orderService.updateStatus(event.getOrderId(), OrderStatus.SHIPPED);
        statusProducerService.publishNotification(eventMapper.toNotificationEvent(order, OrderStatus.SHIPPED, "Your order is on the way.", event.getTrackingNumber()));
        acknowledgment.acknowledge();
    }
}
