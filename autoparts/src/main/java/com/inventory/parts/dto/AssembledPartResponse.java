package com.inventory.parts.dto;

import com.inventory.parts.domain.PartType;
import java.util.List;

public class AssembledPartResponse extends PartResponse {
    private List<ConstituentPartResponse> parts;

    public AssembledPartResponse(String id, String name, PartType type, List<ConstituentPartResponse> parts) {
        setId(id);
        setName(name);
        setType(type);
        this.parts = parts;
    }

    public List<ConstituentPartResponse> getParts() { return parts; }
    public void setParts(List<ConstituentPartResponse> parts) { this.parts = parts; }
}