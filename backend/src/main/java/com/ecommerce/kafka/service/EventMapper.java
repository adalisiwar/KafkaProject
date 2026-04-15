package com.ecommerce.kafka.service;

import com.ecommerce.kafka.event.NotificationEmailEvent;
import com.ecommerce.kafka.event.OrderCreatedEvent;
import com.ecommerce.kafka.event.OrderLineEvent;
import com.ecommerce.kafka.event.OrderPaidEvent;
import com.ecommerce.kafka.event.OrderRejectedEvent;
import com.ecommerce.kafka.event.OrderShippedEvent;
import com.ecommerce.kafka.event.OrderValidatedEvent;
import com.ecommerce.kafka.model.NotificationType;
import com.ecommerce.kafka.model.Order;
import com.ecommerce.kafka.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public OrderCreatedEvent toCreatedEvent(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        fillBase(order, event);
        event.setItems(order.getItems().stream().map(item -> {
            OrderLineEvent lineEvent = new OrderLineEvent();
            lineEvent.setProductId(item.getProduct().getId());
            lineEvent.setProductName(item.getProduct().getName());
            lineEvent.setQuantity(item.getQuantity());
            lineEvent.setUnitPrice(item.getUnitPrice());
            return lineEvent;
        }).collect(Collectors.toList()));
        return event;
    }

    public OrderValidatedEvent toValidatedEvent(Order order, String message) {
        OrderValidatedEvent event = new OrderValidatedEvent();
        fillBase(order, event);
        event.setValidationMessage(message);
        return event;
    }

    public OrderRejectedEvent toRejectedEvent(Order order, String reason) {
        OrderRejectedEvent event = new OrderRejectedEvent();
        fillBase(order, event);
        event.setReason(reason);
        return event;
    }

    public OrderPaidEvent toPaidEvent(Order order, String paymentReference) {
        OrderPaidEvent event = new OrderPaidEvent();
        fillBase(order, event);
        event.setPaymentReference(paymentReference);
        return event;
    }

    public OrderShippedEvent toShippedEvent(Order order, String trackingNumber) {
        OrderShippedEvent event = new OrderShippedEvent();
        fillBase(order, event);
        event.setTrackingNumber(trackingNumber);
        return event;
    }

    public NotificationEmailEvent toNotificationEvent(Order order, OrderStatus status, String message, String trackingNumber) {
        NotificationEmailEvent event = new NotificationEmailEvent();
        fillBase(order, event);
        event.setStatus(status);
        event.setMessage(message);
        event.setTrackingNumber(trackingNumber);
        event.setNotificationType(mapNotificationType(status));
        return event;
    }

    private NotificationType mapNotificationType(OrderStatus status) {
        return switch (status) {
            case VALIDATED -> NotificationType.ORDER_CONFIRMED;
            case REJECTED -> NotificationType.ORDER_REJECTED;
            case PAID -> NotificationType.PAYMENT_SUCCESS;
            case SHIPPED -> NotificationType.ORDER_SHIPPED;
            default -> NotificationType.ORDER_CONFIRMED;
        };
    }

    private void fillBase(Order order, com.ecommerce.kafka.event.BaseOrderEvent event) {
        event.setOrderId(order.getId());
        event.setCustomerId(order.getCustomer().getId());
        event.setCustomerName(order.getCustomer().getName());
        event.setCustomerEmail(order.getCustomer().getEmail());
        event.setTotalAmount(order.getTotalAmount());
        event.setOccurredAt(Instant.now());
    }
}
