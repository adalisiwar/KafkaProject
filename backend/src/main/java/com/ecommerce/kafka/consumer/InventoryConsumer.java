package com.ecommerce.kafka.consumer;

import com.ecommerce.kafka.config.KafkaTopicConfig;
import com.ecommerce.kafka.event.OrderCreatedEvent;
import com.ecommerce.kafka.model.Order;
import com.ecommerce.kafka.model.Product;
import com.ecommerce.kafka.producer.StatusProducerService;
import com.ecommerce.kafka.repository.ProductRepository;
import com.ecommerce.kafka.service.EventMapper;
import com.ecommerce.kafka.service.KafkaLoggingService;
import com.ecommerce.kafka.service.OrderService;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InventoryConsumer {

    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final StatusProducerService statusProducerService;
    private final EventMapper eventMapper;
    private final KafkaLoggingService kafkaLoggingService;

    public InventoryConsumer(ProductRepository productRepository,
                             OrderService orderService,
                             StatusProducerService statusProducerService,
                             EventMapper eventMapper,
                             KafkaLoggingService kafkaLoggingService) {
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.statusProducerService = statusProducerService;
        this.eventMapper = eventMapper;
        this.kafkaLoggingService = kafkaLoggingService;
    }

    @Transactional
    @KafkaListener(
            topics = KafkaTopicConfig.ORDER_CREATED,
            groupId = "inventory-group",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void handle(OrderCreatedEvent event, ConsumerRecord<String, OrderCreatedEvent> record, Acknowledgment acknowledgment) {
        kafkaLoggingService.logConsumer("inventory-group", record, event);
        Order order = orderService.loadDetailed(event.getOrderId());
        Map<Product, Integer> reserved = new HashMap<>();
        for (var item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                statusProducerService.publishRejected(eventMapper.toRejectedEvent(order,
                        "Insufficient stock for " + product.getName()));
                acknowledgment.acknowledge();
                return;
            }
            reserved.put(product, item.getQuantity());
        }

        reserved.forEach((product, quantity) -> product.setStock(product.getStock() - quantity));
        productRepository.saveAll(reserved.keySet());
        statusProducerService.publishValidated(eventMapper.toValidatedEvent(order, "Inventory reserved"));
        acknowledgment.acknowledge();
    }
}
