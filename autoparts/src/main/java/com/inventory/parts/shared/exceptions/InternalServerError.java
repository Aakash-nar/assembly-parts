package com.inventory.parts.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerError extends PartServiceException {
    public InternalServerError(String message) {
        super(message);
    }
}