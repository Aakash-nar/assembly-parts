package com.inventory.parts.service.strategies;

import com.inventory.parts.domain.Part;
import com.inventory.parts.dto.InventoryUpdateResponse;
import com.inventory.parts.repository.PartRepository;

public interface InventoryUpdateStrategy {
    InventoryUpdateResponse addQuantity(Part part, int quantityToAdd, PartRepository partRepository);
}