package com.ecommerce.kafka.event;

public class OrderValidatedEvent extends BaseOrderEvent {

    private String validationMessage;

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    @Override
    public String summary() {
        return "orderId=" + getOrderId() + ", status=VALIDATED";
    }
}
