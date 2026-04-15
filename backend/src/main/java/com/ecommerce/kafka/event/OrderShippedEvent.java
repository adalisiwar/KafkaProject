package com.ecommerce.kafka.event;

public class OrderShippedEvent extends BaseOrderEvent {

    private String trackingNumber;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @Override
    public String summary() {
        return "orderId=" + getOrderId() + ", status=SHIPPED, tracking=" + trackingNumber;
    }
}
