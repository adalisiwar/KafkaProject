package com.ecommerce.kafka.event;

public class OrderPaidEvent extends BaseOrderEvent {

    private String paymentReference;

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    @Override
    public String summary() {
        return "orderId=" + getOrderId() + ", status=PAID, paymentRef=" + paymentReference;
    }
}
