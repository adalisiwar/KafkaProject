package com.ecommerce.kafka.event;

import com.ecommerce.kafka.model.NotificationType;
import com.ecommerce.kafka.model.OrderStatus;

public class NotificationEmailEvent extends BaseOrderEvent {

    private NotificationType notificationType;
    private OrderStatus status;
    private String message;
    private String trackingNumber;

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @Override
    public String summary() {
        return "orderId=" + getOrderId() + ", emailType=" + notificationType;
    }
}
