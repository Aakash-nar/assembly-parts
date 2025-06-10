package com.inventory.parts.dto;

import com.inventory.parts.domain.PartType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Response DTOs
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true,
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RawPartResponse.class, name = "RAW"),
        @JsonSubTypes.Type(value = AssembledPartResponse.class, name = "ASSEMBLED")
})
public abstract class PartResponse {
    private String id;
    private String name;
    private PartType type;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PartType getType() { return type; }
    public void setType(PartType type) { this.type = type; }
}