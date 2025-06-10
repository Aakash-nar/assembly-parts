package com.inventory.parts.dto;

import com.inventory.parts.domain.PartType;

public class CreateRawPartRequest extends CreatePartRequest {
    public CreateRawPartRequest() {
        setType(PartType.RAW);
    }
}