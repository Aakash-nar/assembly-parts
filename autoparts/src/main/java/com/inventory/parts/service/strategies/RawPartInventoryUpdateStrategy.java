package com.inventory.parts.service.strategies;

import com.inventory.parts.domain.Part;
import com.inventory.parts.dto.InventoryUpdateResponse;
import com.inventory.parts.repository.PartRepository;
import org.springframework.stereotype.Component;

@Component
public class RawPartInventoryUpdateStrategy implements InventoryUpdateStrategy {
    @Override
    public InventoryUpdateResponse addQuantity(Part part, int quantityToAdd, PartRepository partRepository) {
        part.setQuantityInStock(part.getQuantityInStock() + quantityToAdd);
        partRepository.save(part);
        return new InventoryUpdateResponse("SUCCESS", null);
    }
}