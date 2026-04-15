package com.ecommerce.kafka.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderRequest {

    @NotNull
    private Long customerId;

    @Valid
    @NotEmpty
    private List<OrderRequestItem> items;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderRequestItem> getItems() {
        return items;
    }

    public void setItems(List<OrderRequestItem> items) {
        this.items = items;
    }

    public static class OrderRequestItem {
        @NotNull
        private Long productId;

        @NotNull
        @Min(1)
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
