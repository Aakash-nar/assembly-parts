package com.inventory.parts.dto;

import jakarta.validation.constraints.Min;

public class AddToInventoryRequest {
    @Min(value = 1, message = "Quantity to add must be at least 1")
    private int quantity;

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}