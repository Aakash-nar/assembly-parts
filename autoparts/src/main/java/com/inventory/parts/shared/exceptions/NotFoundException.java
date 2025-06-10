package com.inventory.parts.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends PartServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}