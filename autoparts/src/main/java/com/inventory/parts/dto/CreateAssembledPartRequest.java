package com.inventory.parts.dto;

import com.inventory.parts.domain.PartType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateAssembledPartRequest extends CreatePartRequest {
    @Valid
    @NotNull(message = "Constituent parts cannot be null for assembled parts")
    private List<ConstituentPartRequest> parts;

    public CreateAssembledPartRequest() {
        setType(PartType.ASSEMBLED);
    }

    public List<ConstituentPartRequest> getParts() { return parts; }
    public void setParts(List<ConstituentPartRequest> parts) { this.parts = parts; }
}