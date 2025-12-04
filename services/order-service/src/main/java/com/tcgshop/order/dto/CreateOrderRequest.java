package com.tcgshop.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;

public class CreateOrderRequest {
    @NotNull
    private Long customerId;

    private Long shippingAddressId;

    @NotNull
    @Valid
    private List<OrderItemRequest> items;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}

