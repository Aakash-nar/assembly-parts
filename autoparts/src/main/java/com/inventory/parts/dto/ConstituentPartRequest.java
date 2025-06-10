package com.inventory.parts.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ConstituentPartRequest {
    @NotBlank(message = "Constituent part ID cannot be empty")
    private String id;
    @Min(value = 1, message = "Constituent quantity must be at least 1")
    private int quantity;

    public ConstituentPartRequest() {}

    public ConstituentPartRequest(String id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}