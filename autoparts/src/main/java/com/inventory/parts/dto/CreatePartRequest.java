package com.inventory.parts.dto;

import com.inventory.parts.domain.PartType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// For creating a part
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true,
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateRawPartRequest.class, name = "RAW"),
        @JsonSubTypes.Type(value = CreateAssembledPartRequest.class, name = "ASSEMBLED")
})
public abstract class CreatePartRequest {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotNull(message = "Type cannot be null")
    private PartType type;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PartType getType() { return type; }
    public void setType(PartType type) { this.type = type; }
}