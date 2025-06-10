package com.inventory.parts.dto;

import com.inventory.parts.domain.PartType;

public class RawPartResponse extends PartResponse {
    public RawPartResponse(String id, String name, PartType type) {
        setId(id);
        setName(name);
        setType(type);
    }
}