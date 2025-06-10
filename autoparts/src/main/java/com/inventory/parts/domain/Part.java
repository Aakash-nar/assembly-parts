package com.inventory.parts.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "parts")
public class Part {
    @Id
    private String id;
    private String name;

    @Enumerated(EnumType.STRING)
    private PartType type;

    private int quantityInStock;

    @OneToMany(mappedBy = "assembledPart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConstituentPart> constituentParts = new ArrayList<>();

    // Constructors
    public Part() {
        this.id = UUID.randomUUID().toString(); // Generate unique ID by default
        this.quantityInStock = 0;
    }

    public Part(String name, PartType type) {
        this(); // Call default constructor to set ID and initial quantity
        this.name = name;
        this.type = type;
    }

    // Getters and Setters (omitted for brevity, but needed for proper JPA functioning)
    // For example:
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PartType getType() { return type; }
    public void setType(PartType type) { this.type = type; }
    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }
    public List<ConstituentPart> getConstituentParts() { return constituentParts; }
    public void setConstituentParts(List<ConstituentPart> constituentParts) { this.constituentParts = constituentParts; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return Objects.equals(id, part.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}