package com.inventory.parts.shared.advice;

public class ErrorResponse {
    private String message;
    private int status;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    // Getters
    public String getMessage() { return message; }
    public int getStatus() { return status; }
}