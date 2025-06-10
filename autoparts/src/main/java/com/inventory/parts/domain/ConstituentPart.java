package com.inventory.parts.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "constituent_parts")
public class ConstituentPart implements Serializable {

    @EmbeddedId
    private ConstituentPartId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("assembledPartId")
    @JoinColumn(name = "assembled_part_id", nullable = false)
    private Part assembledPart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("constituentPartId")
    @JoinColumn(name = "constituent_part_id", nullable = false)
    private Part constituentPart;

    private int requiredQuantity;

    public ConstituentPart() {}

    public ConstituentPart(Part assembledPart, Part constituentPart, int requiredQuantity) {
        this.assembledPart = assembledPart;
        this.constituentPart = constituentPart;
        this.requiredQuantity = requiredQuantity;
        this.id = new ConstituentPartId(assembledPart.getId(), constituentPart.getId());
    }

    public ConstituentPartId getId() { return id; }
    public void setId(ConstituentPartId id) { this.id = id; }
    public Part getAssembledPart() { return assembledPart; }
    public void setAssembledPart(Part assembledPart) { this.assembledPart = assembledPart; }
    public Part getConstituentPart() { return constituentPart; }
    public void setConstituentPart(Part constituentPart) { this.constituentPart = constituentPart; }
    public int getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(int requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    @Embeddable
    public static class ConstituentPartId implements Serializable {
        private String assembledPartId;
        private String constituentPartId;

        public ConstituentPartId() {}

        public ConstituentPartId(String assembledPartId, String constituentPartId) {
            this.assembledPartId = assembledPartId;
            this.constituentPartId = constituentPartId;
        }

        public String getAssembledPartId() { return assembledPartId; }
        public void setAssembledPartId(String assembledPartId) { this.assembledPartId = assembledPartId; }
        public String getConstituentPartId() { return constituentPartId; }
        public void setConstituentPartId(String constituentPartId) { this.constituentPartId = constituentPartId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConstituentPartId that = (ConstituentPartId) o;
            return Objects.equals(assembledPartId, that.assembledPartId) &&
                    Objects.equals(constituentPartId, that.constituentPartId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(assembledPartId, constituentPartId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstituentPart that = (ConstituentPart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}