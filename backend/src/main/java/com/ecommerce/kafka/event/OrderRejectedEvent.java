package com.ecommerce.kafka.event;

public class OrderRejectedEvent extends BaseOrderEvent {

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String summary() {
        return "orderId=" + getOrderId() + ", status=REJECTED, reason=" + reason;
    }
}
