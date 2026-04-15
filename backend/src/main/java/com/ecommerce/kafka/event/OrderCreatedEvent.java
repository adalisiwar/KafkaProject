package com.ecommerce.kafka.event;

import java.util.ArrayList;
import java.util.List;

public class OrderCreatedEvent extends BaseOrderEvent {

    private List<OrderLineEvent> items = new ArrayList<>();

    public List<OrderLineEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderLineEvent> items) {
        this.items = items;
    }

    @Override
    public String summary() {
        return "orderId=" + getOrderId() + ", items=" + items.size() + ", total=" + getTotalAmount();
    }
}
