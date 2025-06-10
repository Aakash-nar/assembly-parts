package com.inventory.parts.shared.exceptions;

public abstract class PartServiceException extends RuntimeException {
    public PartServiceException(String message) {
        super(message);
    }
}